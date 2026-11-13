package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SimulationLog
import com.example.data.model.ExtractionScenario
import com.example.data.model.MviEvaluation
import com.example.data.model.MviWeights
import com.example.data.model.ResourcePoint
import com.example.data.model.RouteComparisonResult
import com.example.data.model.DynamicConstraintComparison
import com.example.data.model.ScientificDataSource
import com.example.data.model.RoverSpecification
import com.example.data.model.RoverTelemetryState
import com.example.data.repository.LunarDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Lazily open Room database on Dispatchers.IO to keep cold start completely off the main thread
    val savedSimulationLogs: StateFlow<List<SimulationLog>> = flow {
        val dao = AppDatabase.getDatabase(getApplication()).simulationLogDao()
        emitAll(dao.getAllLogs())
    }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    // All exploration sites
    val sites: List<ResourcePoint>
        get() = LunarDataRepository.explorationSites

    // Curated PDS and NASA data sources for judge verification (lazy so Frame 1 skips catalog init)
    val scientificDataSources: List<ScientificDataSource> by lazy { LunarDataRepository.scientificDataSources }

    // Focus site (Shackleton Crater & Connecting Ridge)
    private val _selectedSite = MutableStateFlow(sites.first())
    val selectedSite: StateFlow<ResourcePoint> = _selectedSite.asStateFlow()

    // Focus Mission Walkthrough Current Step (0..4)
    // 0: Target Identification, 1: Accessibility Assessment, 2: Route Planning vs Baseline, 3: Mission Simulation, 4: Scientific Report
    private val _focusStep = MutableStateFlow(0)
    val focusStep: StateFlow<Int> = _focusStep.asStateFlow()

    // Dynamic Route Constraints (User can interactively tweak to see why route changes live)
    private val _slopeConstraintDeg = MutableStateFlow(18.0) // Apollo LRV / VIPER 18° baseline
    val slopeConstraintDeg: StateFlow<Double> = _slopeConstraintDeg.asStateFlow()

    private val _batteryReserveConstraintPercent = MutableStateFlow(25.0) // 25% cryogenic return reserve
    val batteryReserveConstraintPercent: StateFlow<Double> = _batteryReserveConstraintPercent.asStateFlow()

    private val _dynamicRouteComparison = MutableStateFlow(
        LunarDataRepository.evaluateDynamicRouteConstraints(18.0, 25.0)
    )
    val dynamicRouteComparison: StateFlow<DynamicConstraintComparison> = _dynamicRouteComparison.asStateFlow()

    // Mining Viability Index Weights
    private val _currentWeights = MutableStateFlow(MviWeights())
    val currentWeights: StateFlow<MviWeights> = _currentWeights.asStateFlow()

    // Computed MVI evaluations for all sites under current weights
    private val _mviEvaluations = MutableStateFlow<Map<String, MviEvaluation>>(emptyMap())
    val mviEvaluations: StateFlow<Map<String, MviEvaluation>> = _mviEvaluations.asStateFlow()

    // Focus Route Comparison (Optimized vs Baseline)
    val routeComparison: RouteComparisonResult by lazy { LunarDataRepository.getFocusMissionRouteComparison() }

    // Rover Engineering Specifications
    private val _roverSpec = MutableStateFlow(RoverSpecification())
    val roverSpec: StateFlow<RoverSpecification> = _roverSpec.asStateFlow()

    // Extraction Scenario
    private val _extractionScenario = MutableStateFlow(ExtractionScenario())
    val extractionScenario: StateFlow<ExtractionScenario> = _extractionScenario.asStateFlow()

    // Rover Telemetry State during Simulation
    private val _telemetryState = MutableStateFlow(LunarDataRepository.simulateTelemetryStep(0.0))
    val telemetryState: StateFlow<RoverTelemetryState> = _telemetryState.asStateFlow()

    // Simulation playing state
    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()

    private var simulationJob: Job? = null

    init {
        recalculateAllMvi(_currentWeights.value)
        // Warm up Room database and lazy catalogs asynchronously after first frame renders
        viewModelScope.launch(Dispatchers.IO) {
            delay(150L)
            scientificDataSources
            routeComparison
            AppDatabase.getDatabase(getApplication())
        }
    }

    fun selectSite(site: ResourcePoint) {
        _selectedSite.value = site
    }

    fun setFocusStep(step: Int) {
        _focusStep.value = step.coerceIn(0, 4)
    }

    fun updateWeights(newWeights: MviWeights) {
        _currentWeights.value = newWeights
        recalculateAllMvi(newWeights)
    }

    fun resetWeights() {
        updateWeights(MviWeights())
    }

    fun updateSlopeConstraint(deg: Double) {
        val clamped = deg.coerceIn(8.0, 35.0)
        _slopeConstraintDeg.value = clamped
        recalculateRouteConstraints(clamped, _batteryReserveConstraintPercent.value)
    }

    fun updateBatteryConstraint(percent: Double) {
        val clamped = percent.coerceIn(5.0, 45.0)
        _batteryReserveConstraintPercent.value = clamped
        recalculateRouteConstraints(_slopeConstraintDeg.value, clamped)
    }

    fun resetRouteConstraints() {
        _slopeConstraintDeg.value = 18.0
        _batteryReserveConstraintPercent.value = 25.0
        recalculateRouteConstraints(18.0, 25.0)
    }

    private fun recalculateRouteConstraints(slopeLimit: Double, batteryReserve: Double) {
        _dynamicRouteComparison.value = LunarDataRepository.evaluateDynamicRouteConstraints(
            slopeLimitDeg = slopeLimit,
            minBatteryReservePercent = batteryReserve
        )
    }

    private fun recalculateAllMvi(weights: MviWeights) {
        val map = mutableMapOf<String, MviEvaluation>()
        sites.forEach { site ->
            map[site.id] = LunarDataRepository.calculateMvi(site, weights)
        }
        _mviEvaluations.value = map
    }

    fun updateExtractionParameters(excavatedKg: Double, iceConcentrationWtPercent: Double) {
        _extractionScenario.value = _extractionScenario.value.copy(
            excavatedRegolithKg = excavatedKg.coerceIn(1000.0, 200000.0),
            assumedIceConcentrationWtPercent = iceConcentrationWtPercent.coerceIn(0.5, 20.0)
        )
    }

    fun getMetadataForLayer(layerIndex: Int) = LunarDataRepository.getMetadataForLayer(layerIndex)

    fun startSimulation() {
        if (_isSimulating.value) return
        _isSimulating.value = true

        simulationJob = viewModelScope.launch {
            var currentHour = _telemetryState.value.elapsedHours
            if (currentHour >= 72.0) currentHour = 0.0

            while (currentHour < 72.0 && _isSimulating.value && isActive) {
                _telemetryState.value = LunarDataRepository.simulateTelemetryStep(currentHour, _roverSpec.value)
                delay(180)
                currentHour = (currentHour + 2.0).coerceAtMost(72.0)
            }

            if (isActive && _isSimulating.value) {
                // Ensure exact 72.0h final tick and only persist log when mission completes
                val finalState = LunarDataRepository.simulateTelemetryStep(72.0, _roverSpec.value)
                _telemetryState.value = finalState
                _isSimulating.value = false

                withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(getApplication()).simulationLogDao().insertLog(
                        SimulationLog(
                            siteName = _selectedSite.value.name,
                            timestampMillis = System.currentTimeMillis(),
                            durationHours = finalState.elapsedHours,
                            distanceKm = finalState.totalDistanceTraveledKm,
                            energyConsumedKWh = finalState.energyBreakdown.netEnergyUsedKWh,
                            batteryRemainingPercent = finalState.batterySoCPercent,
                            volatilesExtractedKg = finalState.accumulatedVolatilesKg,
                            statusSummary = "Traverse completed safely with ${String.format(Locale.US, "%.1f", finalState.batterySoCPercent)}% reserve margin."
                        )
                    )
                }
            } else {
                _isSimulating.value = false
            }
        }
    }

    fun pauseSimulation() {
        _isSimulating.value = false
        simulationJob?.cancel()
    }

    fun resetSimulation() {
        pauseSimulation()
        _telemetryState.value = LunarDataRepository.simulateTelemetryStep(0.0, _roverSpec.value)
    }

    fun stepSimulation() {
        val currentHour = (_telemetryState.value.elapsedHours + 4.0).coerceAtMost(72.0)
        _telemetryState.value = LunarDataRepository.simulateTelemetryStep(currentHour, _roverSpec.value)
    }
}

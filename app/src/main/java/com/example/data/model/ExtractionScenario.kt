package com.example.data.model

/**
 * Water-Ice In-Situ Resource Utilization (ISRU) Extraction Mass Balance Model.
 *
 * Equation:
 *   Recovered Water (kg) = Excavated Regolith (kg) * Ice Concentration (wt%)
 *                        * Thermal Desorption Efficiency (eta_thermal)
 *                        * Condenser Capture Efficiency (eta_condenser)
 */
data class ExtractionScenario(
    val scenarioName: String = "Connecting Ridge to Shackleton Inner PSR Core",
    val excavatedRegolithKg: Double = 75000.0, // 75 metric tons excavated over 30 sols
    val assumedIceConcentrationWtPercent: Double = 5.5, // 5.5 wt% inferred from hydrogen enrichment
    val thermalDesorptionEfficiency: Double = 0.85, // 85% thermal extraction into vapor phase at 400K
    val condenserCaptureEfficiency: Double = 0.90, // 90% cryogenic cold-trap deposition
    val processingEnergyPerKgWaterKWh: Double = 2.8, // Specific sublimation + condensation energy
    val totalProcessingDurationHours: Double = 180.0,
    val storageTankCapacityKg: Double = 4000.0,
    val modelStatusLabel: String = "Simulated Scenario Output (Unvalidated Experimental Model)"
) {
    // Total potential in-situ ice in excavated volume
    val inSituIceMassKg: Double
        get() = excavatedRegolithKg * (assumedIceConcentrationWtPercent / 100.0)

    // Net recovered liquid water equivalent after process losses
    val recoveredWaterKg: Double
        get() = inSituIceMassKg * thermalDesorptionEfficiency * condenserCaptureEfficiency

    // Total electrical/thermal energy demand for volatile processing
    val totalProcessEnergyKWh: Double
        get() = recoveredWaterKg * processingEnergyPerKgWaterKWh

    // Percentage of storage tank utilized
    val storageUtilizationPercent: Double
        get() = (recoveredWaterKg / storageTankCapacityKg) * 100.0
}

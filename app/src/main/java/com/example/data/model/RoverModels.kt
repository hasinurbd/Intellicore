package com.example.data.model

/**
 * Rover engineering parameters and constraints based on NASA VIPER & Apollo LRV geotechnical data.
 */
data class RoverSpecification(
    val roverName: String = "Artemis Polar Prospector - Heavy Scout",
    val maxSlopeLimitDegrees: Double = 18.0, // Hard geotechnical regolith trafficability limit
    val batteryCapacityKWh: Double = 35.0,  // Regenerative cryogenic lithium-sulfur / fuel-cell pack
    val minReturnReservePercent: Double = 25.0, // 25% minimum emergency return energy margin (8.75 kWh)
    val maxSpeedMetersPerSec: Double = 0.15, // Nominal polar traverse speed (~0.54 km/h)
    val locomotionPowerWatts: Double = 220.0, // Nominal drive power on flat terrain
    val psrThermalHeaterWatts: Double = 420.0, // Active thermal loop to prevent avionics freeze (<50K)
    val commsPowerWatts: Double = 95.0,        // X/Ka-band DTE steerable dish power
    val drillPowerWatts: Double = 450.0,        // Percussive deep rotary drill down to 1 m
    val volatileProcessingPowerWatts: Double = 980.0, // Regolith sublimation reactor (heated to ~400K)
    val solarPvPeakPowerWatts: Double = 750.0   // Vertical solar array peak charging in illuminated zones
)

/**
 * Itemized energy consumption breakdown across an exploration segment or traverse.
 */
data class EnergyBudgetBreakdown(
    val locomotionKWh: Double,
    val cryogenicHeatingKWh: Double,
    val communicationsKWh: Double,
    val coreDrillingKWh: Double,
    val volatileProcessingKWh: Double,
    val returnReserveKWh: Double,
    val solarRechargeKWh: Double,
    val netEnergyUsedKWh: Double,
    val remainingBatteryPercent: Double
) {
    val totalExpendedKWh: Double
        get() = locomotionKWh + cryogenicHeatingKWh + communicationsKWh + coreDrillingKWh + volatileProcessingKWh
}

/**
 * State of the simulated rover during a traverse step.
 */
data class RoverTelemetryState(
    val elapsedHours: Double,
    val currentLat: Double,
    val currentLon: Double,
    val currentElevationM: Double,
    val currentSlopeDeg: Double,
    val currentTempKelvin: Double,
    val isInsidePsr: Boolean,
    val batterySoCPercent: Double,
    val totalDistanceTraveledKm: Double,
    val earthLosStatus: Boolean,
    val energyBreakdown: EnergyBudgetBreakdown,
    val accumulatedVolatilesKg: Double
)

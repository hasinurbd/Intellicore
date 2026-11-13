package com.example.data.model

/**
 * Data classification distinguishing raw measured orbital telemetry,
 * derived geochemical proxies, and simulated synthetic model values.
 */
enum class DataSourceClassification(val label: String, val badgeColorHex: Long) {
    MEASURED_OBSERVATION("MEASURED OBSERVATION", 0xFF00B4D8), // Cyan/Blue
    DERIVED_PROXY("DERIVED PROXY", 0xFF9D4EDD),            // Violet/Purple
    SIMULATED_VALUE("SIMULATED VALUE", 0xFFFF9E00)          // Amber/Gold
}

/**
 * Detailed scientific metadata associated with every orbital layer or data point.
 */
data class LayerMetadata(
    val classification: DataSourceClassification,
    val sourceInstrument: String,
    val acquisitionEpoch: String,
    val spatialResolution: String,
    val units: String,
    val processingMethod: String,
    val uncertainty: String,
    val pdsUrl: String? = null
)

/**
 * Comprehensive Lunar South Pole exploration site model with rigorous scientific distinctions.
 */
data class ResourcePoint(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevationMeters: Double,
    val slopeDegrees: Double,
    val maxDiurnalTempKelvin: Double,
    val minDiurnalTempKelvin: Double,
    val solarIlluminationPercent: Double,
    val earthLineOfSightPercent: Double,

    // Geochemical and Resource Distinctions
    val epithermalNeutronCountRate: Double, // cps [MEASURED OBSERVATION - LP-NS / LRO LEND]
    val hydrogenEnrichmentPpm: Double,       // ppm H in upper ~1m regolith [DERIVED PROXY]
    val estimatedWaterEquivalentIceWtPercent: Double, // wt% WEH [DERIVED PROXY with assumptions]
    val titaniumDioxideWtPercent: Double,    // wt% TiO2 oxide abundance [DERIVED PROXY - M3/Clementine spectral inversion]
    val ilmeniteMineralWtPercent: Double,    // wt% FeTiO3 mineral phase (~52.7% TiO2 + 47.3% FeO)
    val helium3ModelEstimatePpb: Double,     // ppb He-3 [SIMULATED MODEL: solar wind flux * ilmenite retention]

    // Terrain classification
    val isPermanentlyShadowedRegion: Boolean,
    val surfaceRoughnessIndex: Double,       // Hurst exponent / micro-slope variance [0..1]

    // Metadata
    val elevationMetadata: LayerMetadata,
    val thermalMetadata: LayerMetadata,
    val resourceMetadata: LayerMetadata
)

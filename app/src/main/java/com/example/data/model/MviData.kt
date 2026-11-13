package com.example.data.model

/**
 * Mining Viability Index (MVI) Weights and Configuration.
 * All weights are normalized to sum to 1.0.
 */
data class MviWeights(
    val resourcePotential: Double = 0.25,     // w_R: Hydrogen enrichment & mineral abundance proxy
    val slopeAccessibility: Double = 0.20,    // w_A: Trafficability (18° hard limit, Apollo/VIPER baseline)
    val extractionFeasibility: Double = 0.15, // w_E: Geomechanical strength, regolith cohesion
    val solarIllumination: Double = 0.15,     // w_S: Photovoltaic surface power availability
    val thermalEmbrittlement: Double = 0.10,  // w_thermal: Cryogenic DBTT penalty below 50K
    val commLineOfSight: Double = 0.10,       // w_comm: Direct-to-Earth (DTE) communication visibility
    val traverseDistance: Double = 0.05       // w_dist: Distance penalty from landing ellipse
) {
    fun normalized(): MviWeights {
        val total = resourcePotential + slopeAccessibility + extractionFeasibility +
                solarIllumination + thermalEmbrittlement + commLineOfSight + traverseDistance
        if (total <= 0.0) return MviWeights()
        return MviWeights(
            resourcePotential / total,
            slopeAccessibility / total,
            extractionFeasibility / total,
            solarIllumination / total,
            thermalEmbrittlement / total,
            commLineOfSight / total,
            traverseDistance / total
        )
    }
}

/**
 * Breakdown of MVI factor scores for transparent scientific evaluation.
 */
data class MviEvaluation(
    val siteId: String,
    val siteName: String,
    val totalScore: Double, // Relative Suitability Score (0-100) under model assumptions
    val resourceSubscore: Double,
    val slopeSubscore: Double,
    val extractionSubscore: Double,
    val solarSubscore: Double,
    val thermalPenalty: Double,
    val commSubscore: Double,
    val distancePenalty: Double,
    val weightsUsed: MviWeights,
    val isCryogenicEmbrittlementRisk: Boolean, // True if T_min < 50 K
    val minTempKelvin: Double,
    val slopeDegrees: Double
) {
    val interpretation: String
        get() = when {
            totalScore >= 75.0 -> "High Relative Suitability (Favorable under current weight assumptions)"
            totalScore >= 50.0 -> "Moderate Suitability (Constrained by thermal or slope factors)"
            else -> "Low Suitability (Severe cryogenic embrittlement or traverse hazards)"
        }
}

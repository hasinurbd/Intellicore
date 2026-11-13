package com.example.data.model

/**
 * Route waypoint with associated terrain and mission attributes.
 */
data class RouteWaypoint(
    val stepIndex: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevationM: Double,
    val slopeDeg: Double,
    val isPsr: Boolean,
    val solarIlluminationPercent: Double,
    val earthLosPercent: Double,
    val cumulativeDistanceKm: Double,
    val estimatedEnergyKWh: Double
)

/**
 * Detailed route execution plan and metrics.
 */
data class RoutePlan(
    val routeName: String,
    val routeType: String, // "Optimized Multi-Objective" vs "Shortest Baseline"
    val algorithmDescription: String,
    val waypoints: List<RouteWaypoint>,
    val totalDistanceKm: Double,
    val maxSlopeDeg: Double,
    val exceedsSlopeThreshold: Boolean, // True if maxSlope > 18°
    val totalEnergyExpendedKWh: Double,
    val solarEnergyRechargedKWh: Double,
    val netBatteryRemainingPercent: Double,
    val meanEarthLosPercent: Double,
    val estimatedWheelSlipRiskPercent: Double,
    val volatileEncounterScore: Double, // 0-100 scientific quality
    val overallSafetyRating: String,
    val primaryFailureRisk: String? = null
)

/**
 * Head-to-head comparison demonstrating the measurable benefit of the optimization algorithm.
 */
data class RouteComparisonResult(
    val studyArea: String = "Shackleton Crater & Connecting Ridge System",
    val optimizedRoute: RoutePlan,
    val baselineRoute: RoutePlan,
    val deltaDistanceKm: Double,
    val deltaEnergyKWh: Double,
    val deltaBatteryRemainingPercent: Double,
    val deltaWheelSlipRiskPercent: Double,
    val deltaVolatileScore: Double,
    val optimizationSummary: String
)

/**
 * Live assessment of a route under dynamic slope & battery constraints.
 */
data class RouteSafetyAssessment(
    val routeName: String,
    val distanceKm: Double,
    val maxSlopeDeg: Double,
    val slopePassed: Boolean,
    val slopeMarginDeg: Double,
    val returnBatteryPercent: Double,
    val batteryPassed: Boolean,
    val batteryMarginPercent: Double,
    val isOverallFeasible: Boolean,
    val primaryFailureReason: String?,
    val decisionLabel: String
)

/**
 * Result of evaluating constraints live (e.g. changing slope limit or battery reserve slider).
 */
data class DynamicConstraintComparison(
    val slopeLimitDeg: Double,
    val minBatteryReservePercent: Double,
    val proposedRouteAssessment: RouteSafetyAssessment,
    val directRouteAssessment: RouteSafetyAssessment,
    val recommendationSummary: String
)

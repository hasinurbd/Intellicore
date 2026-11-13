package com.example

import com.example.data.model.ExtractionScenario
import com.example.data.model.MviWeights
import com.example.data.repository.LunarDataRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LunarDataRepositoryTest {

    @Test
    fun testExplorationSitesContainsShackletonCaseStudy() {
        val sites = LunarDataRepository.explorationSites
        assertTrue("Sites list should not be empty", sites.isNotEmpty())

        val ridge = sites.find { it.id == "shackleton-connecting-ridge" }
        assertNotNull("Shackleton Connecting Ridge must exist", ridge)
        assertEquals(-89.45, ridge!!.latitude, 0.01)
        assertTrue("Connecting Ridge must have safe slope <= 18 deg", ridge.slopeDegrees <= 18.0)
        assertTrue("Connecting Ridge has high solar illumination", ridge.solarIlluminationPercent >= 80.0)

        val psr = sites.find { it.id == "shackleton-interior-psr" }
        assertNotNull("Shackleton Interior PSR must exist", psr)
        assertTrue("PSR is permanently shadowed", psr!!.isPermanentlyShadowedRegion)
        assertTrue("PSR has cryogenic temperature < 50K", psr.minDiurnalTempKelvin < 50.0)
        assertTrue("PSR epithermal neutron count rate indicates hydrogen suppression", psr.epithermalNeutronCountRate < 15.0)
    }

    @Test
    fun testMviEvaluationCalculation() {
        val ridge = LunarDataRepository.explorationSites.first { it.id == "shackleton-connecting-ridge" }
        val weights = MviWeights()
        val evaluation = LunarDataRepository.calculateMvi(ridge, weights)

        assertTrue("MVI total score must be between 0 and 100", evaluation.totalScore in 0.0..100.0)
        assertTrue("Connecting Ridge should have high suitability index", evaluation.totalScore >= 50.0)
        assertEquals("Connecting Ridge (Shackleton-de Gerlache)", evaluation.siteName)
    }

    @Test
    fun testRouteComparisonOptimizedVsBaseline() {
        val comparison = LunarDataRepository.getFocusMissionRouteComparison()
        val opt = comparison.optimizedRoute
        val base = comparison.baselineRoute

        // Optimization must satisfy geotechnical slope limit <= 18 deg
        assertTrue("Optimized max slope must be <= 18 deg", opt.maxSlopeDeg <= 18.0)
        assertTrue("Baseline max slope exceeds 18 deg", base.maxSlopeDeg > 18.0)
        assertTrue("Baseline marked as exceeding slope threshold", base.exceedsSlopeThreshold)

        // Battery reserve
        assertTrue("Optimized battery reserve must maintain safe margin", opt.netBatteryRemainingPercent >= 25.0)
        assertTrue("Baseline route drains battery critically", base.netBatteryRemainingPercent < 25.0)

        // Earth line of sight
        assertTrue("Optimized route maintains superior Earth LOS", opt.meanEarthLosPercent > base.meanEarthLosPercent)
    }

    @Test
    fun testExtractionScenarioMassBalance() {
        val scenario = ExtractionScenario(
            excavatedRegolithKg = 75000.0,
            assumedIceConcentrationWtPercent = 5.5,
            thermalDesorptionEfficiency = 0.85,
            condenserCaptureEfficiency = 0.90
        )

        // In-situ ice = 75,000 * 0.055 = 4,125 kg
        assertEquals(4125.0, scenario.inSituIceMassKg, 0.01)

        // Recovered = 4,125 * 0.85 * 0.90 = 3,155.625 kg
        assertEquals(3155.625, scenario.recoveredWaterKg, 0.01)
    }

    @Test
    fun testScientificDataSourcesCatalog() {
        val sources = LunarDataRepository.scientificDataSources
        assertEquals("Must have all 8 verified scientific datasets", 8, sources.size)

        val lola = sources.find { it.id == "lro-lola-polar-dem" }
        assertNotNull("LOLA dataset must exist", lola)
        assertTrue("LOLA must be MEASURED_OBSERVATION", lola!!.classification == com.example.data.model.DataSourceClassification.MEASURED_OBSERVATION)
        assertTrue("LOLA must have PDS ID", lola.pdsProductIdentifier.contains("LRO-L-LOLA"))
        assertTrue("LOLA must have caveats", lola.whatItDoesNotProve.isNotEmpty())

        val lend = sources.find { it.id == "lro-lend-neutron" }
        assertNotNull("LRO LEND neutron proxy must exist", lend)
        assertTrue("LEND must be DERIVED_PROXY", lend!!.classification == com.example.data.model.DataSourceClassification.DERIVED_PROXY)
        assertTrue("LEND must clarify hydrogen vs water ice", lend.whatItDoesNotProve.contains("water ice"))

        val lpNs = sources.find { it.id == "lp-ns-hydrogen" }
        assertNotNull("Lunar Prospector NS dataset must exist", lpNs)
        assertTrue("LP-NS must be DERIVED_PROXY", lpNs!!.classification == com.example.data.model.DataSourceClassification.DERIVED_PROXY)

        val viper = sources.find { it.id == "viper-apollo-geotechnical" }
        assertNotNull("VIPER geotechnical standard must exist", viper)
        assertTrue("VIPER standard must explain 18 deg limit", viper!!.roleInDecisionMaking.contains("18°"))

        val expectedPdsUrls = listOf(
            "https://pds-geosciences.wustl.edu/missions/lro/lola.htm",
            "https://pds-geosciences.wustl.edu/missions/lro/diviner.htm",
            "https://pds-geosciences.wustl.edu/missions/lro/lend.htm",
            "https://pds-geosciences.wustl.edu/missions/lunarp",
            "https://pds-geosciences.wustl.edu/missions/clementine",
            "https://pds-imaging.jpl.nasa.gov/data/m3"
        )
        val actualUrls = sources.map { it.pdsUrl }.toSet()
        expectedPdsUrls.forEach { expected ->
            assertTrue("Expected NASA PDS URL missing from catalog: $expected", expected in actualUrls)
        }
    }

    @Test
    fun testGetMetadataForLayerMatchesActiveLayers() {
        val lolaMeta = LunarDataRepository.getMetadataForLayer(0)
        assertTrue(lolaMeta.sourceInstrument.contains("LOLA"))
        assertEquals("https://pds-geosciences.wustl.edu/missions/lro/lola.htm", lolaMeta.pdsUrl)

        val divinerMeta = LunarDataRepository.getMetadataForLayer(1)
        assertTrue(divinerMeta.sourceInstrument.contains("Diviner"))
        assertEquals("https://pds-geosciences.wustl.edu/missions/lro/diviner.htm", divinerMeta.pdsUrl)

        val neutronMeta = LunarDataRepository.getMetadataForLayer(2)
        assertTrue(neutronMeta.sourceInstrument.contains("LEND"))
        assertEquals("https://pds-geosciences.wustl.edu/missions/lro/lend.htm", neutronMeta.pdsUrl)

        val spectralMeta = LunarDataRepository.getMetadataForLayer(3)
        assertTrue(spectralMeta.sourceInstrument.contains("M³"))
        assertEquals("https://pds-imaging.jpl.nasa.gov/data/m3", spectralMeta.pdsUrl)
    }

    @Test
    fun testTelemetryStepSolarRechargeMonotonicAndPsrSafe() {
        val atPsrEntry = LunarDataRepository.simulateTelemetryStep(46.8)
        val atMissionEnd = LunarDataRepository.simulateTelemetryStep(72.0)

        assertTrue("Solar recharge on sunlit ridge must reach ~14.2 kWh", atPsrEntry.energyBreakdown.solarRechargeKWh >= 14.0)
        assertEquals(
            "Solar recharge must remain preserved after entering PSR",
            atPsrEntry.energyBreakdown.solarRechargeKWh,
            atMissionEnd.energyBreakdown.solarRechargeKWh,
            0.001
        )
        assertTrue("Core drilling energy must be positive at 72h", atMissionEnd.energyBreakdown.coreDrillingKWh > 0.0)
        assertTrue("Volatile processing energy must be positive at 72h", atMissionEnd.energyBreakdown.volatileProcessingKWh > 0.0)
        assertTrue("Final battery SoC must stay well above 25% emergency reserve", atMissionEnd.batterySoCPercent >= 25.0)
    }

    @Test
    fun testDynamicRouteConstraintEvaluation() {
        // Standard NASA VIPER baseline (18° slope, 25% battery reserve)
        val baselineEval = LunarDataRepository.evaluateDynamicRouteConstraints(
            slopeLimitDeg = 18.0,
            minBatteryReservePercent = 25.0
        )
        assertTrue("Proposed route must be feasible under 18° and 25%", baselineEval.proposedRouteAssessment.isOverallFeasible)
        assertTrue("Direct route must be REJECTED under 18° and 25%", !baselineEval.directRouteAssessment.isOverallFeasible)
        assertTrue("Direct route fails slope (29.4° > 18°)", !baselineEval.directRouteAssessment.slopePassed)
        assertTrue("Direct route fails battery (9.1% < 25%)", !baselineEval.directRouteAssessment.batteryPassed)

        // Super-strict slope constraint: 10°
        val strictEval = LunarDataRepository.evaluateDynamicRouteConstraints(
            slopeLimitDeg = 10.0,
            minBatteryReservePercent = 25.0
        )
        assertTrue("Proposed route max slope (13.8°) exceeds 10° limit", !strictEval.proposedRouteAssessment.slopePassed)
        assertTrue("Proposed route marked not overall feasible under 10°", !strictEval.proposedRouteAssessment.isOverallFeasible)

        // Relaxed slope constraint: 32° (direct route slope passes, but battery still fails!)
        val relaxedSlopeEval = LunarDataRepository.evaluateDynamicRouteConstraints(
            slopeLimitDeg = 32.0,
            minBatteryReservePercent = 25.0
        )
        assertTrue("Direct route passes relaxed 32° slope", relaxedSlopeEval.directRouteAssessment.slopePassed)
        assertTrue("Direct route STILL FAILS battery (9.1% < 25%)", !relaxedSlopeEval.directRouteAssessment.batteryPassed)
        assertTrue("Direct route is STILL rejected overall", !relaxedSlopeEval.directRouteAssessment.isOverallFeasible)
    }
}

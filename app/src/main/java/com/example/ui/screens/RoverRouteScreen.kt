package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FormulaDisplayCard
import com.example.ui.components.MetricCompareRow
import com.example.ui.theme.ArtemisIcons
import com.example.ui.theme.ArtemisOrange
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LunarBorder
import com.example.ui.theme.LunarDeepNavy
import com.example.ui.theme.LunarSurfaceDark
import com.example.ui.theme.LunarSurfaceElevated
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.SolarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VolatileIceAqua
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun RoverRouteScreen(viewModel: MainViewModel) {
    val routeComparison = viewModel.routeComparison
    val opt = routeComparison.optimizedRoute
    val base = routeComparison.baselineRoute
    val spec by viewModel.roverSpec.collectAsStateWithLifecycle()
    val scenario by viewModel.extractionScenario.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LunarDeepNavy)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceElevated),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ROVER ROUTE OPTIMIZATION & EXTRACTION",
                        style = MaterialTheme.typography.titleMedium,
                        color = ArtemisOrange,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Algorithmic comparison against unconstrained shortest baseline route, rover energy budget justification, and transparent volatile mass balance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Optimization Benefit Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LunarBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Optimization Benefit Demonstration",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .background(SafeEmerald.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "A* MULTI-OBJECTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = SafeEmerald,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 9.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = routeComparison.optimizationSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = LunarBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    MetricCompareRow(
                        label = "Distance",
                        optimizedValue = "${opt.totalDistanceKm} km",
                        baselineValue = "${base.totalDistanceKm} km",
                        isOptimizedFavorable = false,
                        deltaText = "+${String.format(Locale.US, "%.1f", routeComparison.deltaDistanceKm)} km"
                    )
                    MetricCompareRow(
                        label = "Max Slope (18° Hard Limit)",
                        optimizedValue = "${opt.maxSlopeDeg}° (Safe)",
                        baselineValue = "${base.maxSlopeDeg}° (EXCEEDED)",
                        isOptimizedFavorable = true,
                        deltaText = "-15.6°"
                    )
                    MetricCompareRow(
                        label = "Solar PV Energy Top-Up",
                        optimizedValue = "+${opt.solarEnergyRechargedKWh} kWh",
                        baselineValue = "+${base.solarEnergyRechargedKWh} kWh",
                        isOptimizedFavorable = true,
                        deltaText = "+12.1 kWh"
                    )
                    MetricCompareRow(
                        label = "Battery Margin at Target",
                        optimizedValue = "${opt.netBatteryRemainingPercent.toInt()}%",
                        baselineValue = "${base.netBatteryRemainingPercent.toInt()}%",
                        isOptimizedFavorable = true,
                        deltaText = "+${routeComparison.deltaBatteryRemainingPercent.toInt()}%"
                    )
                    MetricCompareRow(
                        label = "Wheel Slip Immobility Risk",
                        optimizedValue = "${opt.estimatedWheelSlipRiskPercent.toInt()}%",
                        baselineValue = "${base.estimatedWheelSlipRiskPercent.toInt()}%",
                        isOptimizedFavorable = true,
                        deltaText = "${routeComparison.deltaWheelSlipRiskPercent.toInt()}%"
                    )
                }
            }
        }

        // Justification of Rover Specifications
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SolarGold.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Rover Specification & Engineering Justification",
                        style = MaterialTheme.typography.titleSmall,
                        color = SolarGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• 18° Slope Limit: Grounded in NASA Apollo LRV geotechnical data and VIPER mobility testing. The internal friction angle of polar lunar regolith is phi ≈ 35°. Applying a structural safety factor of 1.8 establishes 18° as the threshold beyond which wheel sinkage and slip exceed 60%, risking rover stall.\n\n" +
                                "• 35 kWh Battery Capacity: Heavy ISRU prospecting architecture. Unlike VIPER's smaller 1.2 kWh reconnaissance battery, a multi-week volatile extraction demonstrator incorporates a 35 kWh regenerative cryogenic lithium-sulfur / fuel-cell pack to power heaters, deep percussive coring, and thermal reactors.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LunarDeepNavy, RoundedCornerShape(6.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Capacity", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${spec.batteryCapacityKWh} kWh", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontFamily = JetBrainsMonoFontFamily)
                        }
                        Column {
                            Text("Protected Return Reserve", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${spec.minReturnReservePercent.toInt()}% (8.75 kWh)", style = MaterialTheme.typography.bodyMedium, color = SolarGold, fontFamily = JetBrainsMonoFontFamily)
                        }
                        Column {
                            Text("Peak PV Solar Input", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${spec.solarPvPeakPowerWatts.toInt()} W", style = MaterialTheme.typography.bodyMedium, color = SafeEmerald, fontFamily = JetBrainsMonoFontFamily)
                        }
                    }
                }
            }
        }

        // Water-Ice Extraction Mass Balance Model
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VolatileIceAqua.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Water-Ice Extraction Mass Balance",
                            style = MaterialTheme.typography.titleSmall,
                            color = VolatileIceAqua,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(imageVector = ArtemisIcons.WaterDrop, contentDescription = null, tint = VolatileIceAqua)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(WarningAmber.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = scenario.modelStatusLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = WarningAmber,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 9.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FormulaDisplayCard(
                        title = "ISRU Mass Balance Equation",
                        equation = "Mass_recovered = Mass_regolith * C_ice * eta_desorption * eta_condenser",
                        description = "Simulates total recovered liquid water equivalent factoring in thermal sublimation yield at 400K and cryogenic cold-finger condenser efficiency.",
                        variables = listOf(
                            "Mass_regolith" to "${scenario.excavatedRegolithKg.toInt()} kg excavated regolith",
                            "C_ice" to "${scenario.assumedIceConcentrationWtPercent}% assumed ice concentration (derived proxy)",
                            "eta_desorption" to "${(scenario.thermalDesorptionEfficiency * 100).toInt()}% thermal desorption efficiency into vapor",
                            "eta_condenser" to "${(scenario.condenserCaptureEfficiency * 100).toInt()}% condenser cold-finger capture efficiency"
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Interactive Sliders
                    Text(
                        text = "Excavated Regolith Tonnage: ${(scenario.excavatedRegolithKg / 1000.0).toInt()} metric tons",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Slider(
                        value = (scenario.excavatedRegolithKg / 1000.0).toFloat(),
                        onValueChange = { viewModel.updateExtractionParameters(it.toDouble() * 1000.0, scenario.assumedIceConcentrationWtPercent) },
                        valueRange = 10f..150f,
                        colors = SliderDefaults.colors(thumbColor = VolatileIceAqua, activeTrackColor = VolatileIceAqua)
                    )

                    Text(
                        text = "Assumed Ice Concentration: ${String.format(Locale.US, "%.1f", scenario.assumedIceConcentrationWtPercent)} wt%",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Slider(
                        value = scenario.assumedIceConcentrationWtPercent.toFloat(),
                        onValueChange = { viewModel.updateExtractionParameters(scenario.excavatedRegolithKg, it.toDouble()) },
                        valueRange = 1.0f..12.0f,
                        colors = SliderDefaults.colors(thumbColor = VolatileIceAqua, activeTrackColor = VolatileIceAqua)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LunarDeepNavy, RoundedCornerShape(6.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Recovered Water", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${String.format(Locale.US, "%.1f", scenario.recoveredWaterKg)} kg H2O", style = MaterialTheme.typography.bodyMedium, color = VolatileIceAqua, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Process Energy", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${String.format(Locale.US, "%.1f", scenario.totalProcessEnergyKWh)} kWh", style = MaterialTheme.typography.bodyMedium, color = SolarGold, fontFamily = JetBrainsMonoFontFamily)
                        }
                        Column {
                            Text("Tank Fill", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${String.format(Locale.US, "%.1f", scenario.storageUtilizationPercent)}%", style = MaterialTheme.typography.bodyMedium, color = SafeEmerald, fontFamily = JetBrainsMonoFontFamily)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

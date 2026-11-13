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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MviEvaluation
import com.example.ui.components.FormulaDisplayCard
import com.example.ui.components.MiningViabilityModal
import com.example.ui.theme.ArtemisIcons
import com.example.ui.theme.ArtemisOrange
import com.example.ui.theme.CryogenicViolet
import com.example.ui.theme.HazardCrimson
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LunarBorder
import com.example.ui.theme.LunarDeepNavy
import com.example.ui.theme.LunarSurfaceDark
import com.example.ui.theme.LunarSurfaceElevated
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun MiningIndexScreen(viewModel: MainViewModel) {
    val weights by viewModel.currentWeights.collectAsStateWithLifecycle()
    val evaluations by viewModel.mviEvaluations.collectAsStateWithLifecycle()

    var showEmbrittlementDialog by remember { mutableStateOf(false) }

    // Sorted evaluations by descending total score (memoized)
    val sortedEvaluations = remember(evaluations) {
        evaluations.values.sortedByDescending { it.totalScore }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LunarDeepNavy)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header Card with Transparency Disclaimer
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceElevated),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MINING VIABILITY INDEX (MVI)",
                            style = MaterialTheme.typography.titleMedium,
                            color = ArtemisOrange,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showEmbrittlementDialog = true }) {
                            Icon(
                                imageVector = ArtemisIcons.Thermostat,
                                contentDescription = "Explain Thermal Embrittlement",
                                tint = CryogenicViolet
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Transparent multi-criteria decision model. Notice: Scores represent relative suitability (0-100) under specified engineering assumptions—NOT a probability of mining success.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Transparent Equation Card
        item {
            FormulaDisplayCard(
                title = "MVI Formulation & Normalization",
                equation = "MVI = w_R*S_R + w_A*S_A + w_E*S_E + w_S*S_S - w_thermal*P_thermal + w_comm*S_comm - w_dist*P_dist",
                description = "Each subfactor is normalized to a 0-100 scale. Normalized weights satisfy Sum(w_i) = 1.0. Hard geotechnical penalty applied if slope > 18°.",
                variables = listOf(
                    "S_R (Resource)" to "Epithermal neutron suppression (0-80 pts) + Ilmenite wt% (0-20 pts)",
                    "S_A (Access)" to "Trafficability score: 100 pts for <=8°, drops to 0 at 18° geotechnical limit",
                    "S_E (Extraction)" to "Regolith cohesion & boulder-free surface fraction (0-100 pts)",
                    "S_S (Solar)" to "Diurnal illumination percentage for photovoltaic power generation (0-100 pts)",
                    "P_thermal" to "Cryogenic embrittlement penalty: scales exponentially when T_min < 50 K",
                    "S_comm (Comms)" to "Line-of-sight visibility percentage to Earth DTE ground stations",
                    "P_dist (Traverse)" to "Topographical penalty for deep crater descents (>3000 m)"
                )
            )
        }

        // Weight Sensitivity Tool
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
                            text = "Weight Sensitivity Analysis Tool",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { viewModel.resetWeights() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                            Text("Reset", color = ArtemisOrange)
                        }
                    }
                    Text(
                        text = "Test whether changing factor weights substantially changes the preferred site ranking.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    WeightSlider(
                        label = "Resource Weight (w_R)",
                        value = weights.resourcePotential,
                        onValueChange = { viewModel.updateWeights(weights.copy(resourcePotential = it)) }
                    )

                    WeightSlider(
                        label = "Slope Accessibility (w_A)",
                        value = weights.slopeAccessibility,
                        onValueChange = { viewModel.updateWeights(weights.copy(slopeAccessibility = it)) }
                    )

                    WeightSlider(
                        label = "Solar Illumination (w_S)",
                        value = weights.solarIllumination,
                        onValueChange = { viewModel.updateWeights(weights.copy(solarIllumination = it)) }
                    )

                    WeightSlider(
                        label = "Thermal Embrittlement (w_thermal)",
                        value = weights.thermalEmbrittlement,
                        onValueChange = { viewModel.updateWeights(weights.copy(thermalEmbrittlement = it)) }
                    )
                }
            }
        }

        // Ranking Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ranked Site Suitability",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${sortedEvaluations.size} Sites Evaluated",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        // Ranked Evaluation Cards
        items(sortedEvaluations, key = { it.siteId }) { eval ->
            MviSiteCard(eval)
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Thermal Embrittlement Explanatory Modal
    if (showEmbrittlementDialog) {
        MiningViabilityModal(onDismiss = { showEmbrittlementDialog = false })
    }
}

@Composable
private fun WeightSlider(label: String, value: Double, onValueChange: (Double) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
            Text(String.format(Locale.US, "%.2f", value), style = MaterialTheme.typography.labelSmall, color = ArtemisOrange, fontFamily = JetBrainsMonoFontFamily)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble()) },
            valueRange = 0.0f..1.0f,
            colors = SliderDefaults.colors(
                thumbColor = ArtemisOrange,
                activeTrackColor = ArtemisOrange,
                inactiveTrackColor = LunarBorder
            )
        )
    }
}

@Composable
private fun MviSiteCard(eval: MviEvaluation) {
    val scoreColor = when {
        eval.totalScore >= 75.0 -> SafeEmerald
        eval.totalScore >= 50.0 -> WarningAmber
        else -> HazardCrimson
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = eval.siteName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = eval.interpretation,
                        style = MaterialTheme.typography.bodySmall,
                        color = scoreColor,
                        fontSize = 11.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(scoreColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", eval.totalScore)} / 100",
                        style = MaterialTheme.typography.titleMedium,
                        color = scoreColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = LunarBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Subscore Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Resource", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text("${eval.resourceSubscore.toInt()} pts", style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontFamily = JetBrainsMonoFontFamily)
                }
                Column {
                    Text("Slope Access", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text("${eval.slopeSubscore.toInt()} pts", style = MaterialTheme.typography.bodySmall, color = if (eval.slopeDegrees > 18.0) HazardCrimson else TextPrimary, fontFamily = JetBrainsMonoFontFamily)
                }
                Column {
                    Text("Solar Power", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text("${eval.solarSubscore.toInt()} pts", style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontFamily = JetBrainsMonoFontFamily)
                }
                Column {
                    Text("Thermal Penalty", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text("-${eval.thermalPenalty.toInt()} pts", style = MaterialTheme.typography.bodySmall, color = if (eval.isCryogenicEmbrittlementRisk) CryogenicViolet else TextPrimary, fontFamily = JetBrainsMonoFontFamily)
                }
            }

            if (eval.isCryogenicEmbrittlementRisk) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = ArtemisIcons.Thermostat, contentDescription = null, tint = CryogenicViolet, modifier = Modifier.height(14.dp).width(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cryogenic Risk: T_min = ${eval.minTempKelvin} K (<50 K ductile-to-brittle transition)",
                        style = MaterialTheme.typography.labelSmall,
                        color = CryogenicViolet,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

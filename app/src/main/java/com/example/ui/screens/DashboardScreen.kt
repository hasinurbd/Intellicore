package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ArtemisOrange
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LunarBorder
import com.example.ui.theme.LunarDeepNavy
import com.example.ui.theme.LunarSurfaceDark
import com.example.ui.theme.LunarSurfaceElevated
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.ScientificCyan
import com.example.ui.theme.SolarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ArtemisIcons
import com.example.ui.theme.VolatileIceAqua

private val CardShape12 = RoundedCornerShape(12.dp)
private val CardShape10 = RoundedCornerShape(10.dp)
private val CardBorderDefault = BorderStroke(1.dp, LunarBorder)
private val CardBorderHero = BorderStroke(1.5.dp, ArtemisOrange)

@Composable
fun DashboardScreen(
    onNavigateToFocusMission: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToMvi: () -> Unit,
    onNavigateToRoute: () -> Unit,
    onNavigateToRoadmap: () -> Unit,
    onNavigateToSources: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LunarDeepNavy)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Banner & Archived Orbital Data Header
        Card(
            colors = CardDefaults.cardColors(containerColor = LunarSurfaceElevated),
            shape = CardShape12
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_artemis_badge),
                            contentDescription = "Artemis Badge",
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ARTEMIS RESOURCE NAVIGATOR",
                                style = MaterialTheme.typography.titleMedium,
                                color = ArtemisOrange,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Lunar South Pole ISRU Analytics",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(ScientificCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .border(1.dp, ScientificCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "ARCHIVED DATA",
                                style = MaterialTheme.typography.labelSmall,
                                color = ScientificCyan,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(SafeEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .border(1.dp, SafeEmerald.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "100% OFFLINE VERIFIED",
                                style = MaterialTheme.typography.labelSmall,
                                color = SafeEmerald,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Interactive analysis of archived orbital telemetry (NASA LRO LOLA, Diviner, LEND, and Chandrayaan-1 M3). Synthetic rover telemetry is generated via physics-based state-space simulations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
        }
        // Focus Mission Highlight Hero Card
        Card(
            colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
            border = CardBorderHero,
            shape = CardShape12
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRIMARY STUDY MISSION",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArtemisOrange,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                        Box(
                            modifier = Modifier
                                .background(SafeEmerald.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("5-STEP WORKFLOW", style = MaterialTheme.typography.labelSmall, color = SafeEmerald, fontSize = 9.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Shackleton Crater & Connecting Ridge",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Complete exploration sequence: target identification, geotechnical slope accessibility, route planning vs. baseline, energy simulation, and scientific dossier generation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onNavigateToFocusMission,
                        colors = ButtonDefaults.buttonColors(containerColor = ArtemisOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = ArtemisIcons.Explore, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text("Launch Focus Mission Walkthrough", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
        }
        // Key South Polar Environmental Telemetry Cards
        Text(
            text = "Polar Environmental Telemetry",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Diviner Thermal Cold Trap
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
                border = CardBorderDefault,
                shape = CardShape10
            ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Cold Trap Temp", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("38 K (-235°C)", style = MaterialTheme.typography.bodyLarge, color = VolatileIceAqua, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Diviner Observation", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                    }
                }

            // Epithermal Neutron Flux
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
                border = CardBorderDefault,
                shape = CardShape10
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Hydrogen Proxy", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("610 ppm H", style = MaterialTheme.typography.bodyLarge, color = ScientificCyan, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("LP-NS / LEND Proxy", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                }
            }
        }

        // Module Navigation Cards
        Text(
            text = "Exploration Subsystems",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NavModuleCard(
                    title = "Mining Viability Index (MVI)",
                    subtitle = "Transparent equation, weights, and cryogenic thermal embrittlement",
                    icon = ArtemisIcons.Assessment,
                    accentColor = ArtemisOrange,
                    onClick = onNavigateToMvi
                )
                NavModuleCard(
                    title = "Rover Route & Extraction Model",
                    subtitle = "Optimized vs baseline comparison, 18° slope limit, mass balance",
                    icon = ArtemisIcons.Route,
                    accentColor = SafeEmerald,
                    onClick = onNavigateToRoute
                )
                NavModuleCard(
                    title = "Orbital Map & Calibration Layers",
                    subtitle = "LOLA altimetry, Diviner temperatures, M³ mineralogy metadata",
                    icon = ArtemisIcons.Map,
                    accentColor = ScientificCyan,
                    onClick = onNavigateToMap
                )
                NavModuleCard(
                    title = "Scientific Data Sources & PDS Citations",
                    subtitle = "NASA planetary archives, LOLA DEM, Diviner thermal, 100% offline verified",
                    icon = ArtemisIcons.Science,
                    accentColor = ScientificCyan,
                    onClick = onNavigateToSources
                )
            NavModuleCard(
                title = "Prioritized Scientific Roadmap",
                subtitle = "Uncertainty modeling, energy validation, sub-orbital rocket hoppers",
                icon = ArtemisIcons.RocketLaunch,
                accentColor = SolarGold,
                onClick = onNavigateToRoadmap
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun NavModuleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
        border = CardBorderDefault,
        shape = CardShape10
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted)
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArtemisIcons
import com.example.ui.theme.ArtemisOrange
import com.example.ui.theme.HazardCrimson
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

@Composable
fun RoadmapScreen() {
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
                        text = "SCIENTIFIC & ENGINEERING ROADMAP",
                        style = MaterialTheme.typography.titleMedium,
                        color = ArtemisOrange,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rigorous prioritization focusing on physical validity, error propagation, and orbital sensor calibration over cosmetic or non-physical features.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Physical Vehicle Clarification: Rocket Hopper vs Aerodynamic Drones
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SolarGold.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Physical Aerial Vehicle Specification",
                            style = MaterialTheme.typography.titleSmall,
                            color = SolarGold,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(imageVector = ArtemisIcons.RocketLaunch, contentDescription = null, tint = SolarGold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HazardCrimson.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = HazardCrimson, modifier = Modifier.height(16.dp).width(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PHYSICAL CONSTRAINT: Aerodynamic propeller drones CANNOT operate in the lunar near-vacuum (P < 10^-10 Pa).",
                                style = MaterialTheme.typography.labelSmall,
                                color = HazardCrimson,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sub-Orbital Ballistic Rocket Hopper Architecture:\n" +
                                "To access extreme crater walls (>30° slope) and deep PSR floors inaccessible to rovers, exploration relies on a Sub-Orbital Cold-Gas (GN2) or Monopropellant Hydrazine Rocket Hopper. Pulsed reaction control thrusters provide throttled delta-V for parabolic ballistic hops (500m–2km) without atmospheric aerodynamic dependency.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Prioritized Phases
        item {
            Text(
                text = "Development Priorities",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            RoadmapPhaseCard(
                phaseNumber = "PHASE 1 (TOP PRIORITY)",
                title = "Monte Carlo Uncertainty Analysis",
                description = "Propagate LOLA vertical DEM errors (±1.2m), Diviner calibration uncertainties (±2.5K at 40K), and neutron Poisson counting statistics (±15%) through the MVI equation to generate 95% confidence intervals for suitability rankings.",
                status = "In Active Development",
                statusColor = SafeEmerald
            )
        }

        item {
            RoadmapPhaseCard(
                phaseNumber = "PHASE 2 (TOP PRIORITY)",
                title = "Cryogenic Battery & Heat Pipe Energy Simulation",
                description = "Implement detailed multi-sol transient thermal models capturing lunar libration cycles, regolith thermal conductivity, phase-change heat pipes, and battery cell degradation at cryogenic boundary layers.",
                status = "High Priority",
                statusColor = ArtemisOrange
            )
        }

        item {
            RoadmapPhaseCard(
                phaseNumber = "PHASE 3 (HIGH PRIORITY)",
                title = "LROC NAC 0.5m/pixel Route Validation",
                description = "Integrate sub-meter LROC Narrow Angle Camera stereo photogrammetry DEMs to identify micro-boulders (>0.5m) and localized wheel sinkage hazards along the Connecting Ridge traverse.",
                status = "High Priority",
                statusColor = ScientificCyan
            )
        }

        item {
            RoadmapPhaseCard(
                phaseNumber = "PHASE 4 (MEDIUM PRIORITY)",
                title = "Percussive Drill Soil Mechanics Modeling",
                description = "Couple rotary percussive drill torque and penetration resistance with cryogenic regolith cohesive shear models down to 1 meter depth.",
                status = "Planned",
                statusColor = TextMuted
            )
        }

        item {
            RoadmapPhaseCard(
                phaseNumber = "DE-PRIORITIZED",
                title = "VR / AR Immersion Features",
                description = "Virtual/Augmented Reality headsets and 3D visual effects are formally deprioritized to preserve engineering focus on physical fidelity and numerical validation.",
                status = "Deprioritized",
                statusColor = HazardCrimson
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun RoadmapPhaseCard(
    phaseNumber: String,
    title: String,
    description: String,
    status: String,
    statusColor: Color
) {
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
                Text(
                    text = phaseNumber,
                    style = MaterialTheme.typography.labelSmall,
                    color = ArtemisOrange,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.sp
                )
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}

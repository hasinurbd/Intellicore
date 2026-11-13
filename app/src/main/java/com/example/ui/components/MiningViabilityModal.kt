package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArtemisIcons
import com.example.ui.theme.ArtemisOrange
import com.example.ui.theme.CryogenicViolet
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LunarBorder
import com.example.ui.theme.LunarSurfaceDark
import com.example.ui.theme.LunarSurfaceElevated
import com.example.ui.theme.ScientificCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun MiningViabilityModal(
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = LunarSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, ArtemisOrange.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ArtemisIcons.Thermostat,
                            contentDescription = null,
                            tint = CryogenicViolet
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MVI Transparency & Methodology",
                            style = MaterialTheme.typography.titleMedium,
                            color = ArtemisOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(color = LunarBorder)

                // Formula Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LunarSurfaceDark, RoundedCornerShape(8.dp))
                        .border(1.dp, LunarBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "MVI Mathematical Formulation",
                            style = MaterialTheme.typography.labelSmall,
                            color = ScientificCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MVI = w_R·S_R + w_A·S_A + w_E·S_E + w_S·S_S - w_thermal·P_thermal + w_comm·S_comm - w_dist·P_dist",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = JetBrainsMonoFontFamily,
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Thermal Embrittlement Explanation
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Thermal Embrittlement Factor (Cryogenic Safety)",
                        style = MaterialTheme.typography.titleSmall,
                        color = CryogenicViolet,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Permanently Shadowed Regions (PSRs) such as Shackleton Crater floor drop below 50 K. At these extreme temperatures, metals and elastomeric seals undergo Ductile-to-Brittle Transition (DBTT), reducing fracture toughness by ~70% and increasing risk of catastrophic mechanical failure. The MVI model applies an exponential penalty when temperatures drop below 50 K.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }

                // Disclaimer Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarningAmber.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, WarningAmber.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "IMPORTANT DISCLAIMER: An MVI score (e.g. 85/100) represents a Relative Suitability Index under specified model weights and engineering assumptions—NOT an absolute guarantee of mining success.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningAmber,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = ArtemisOrange, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

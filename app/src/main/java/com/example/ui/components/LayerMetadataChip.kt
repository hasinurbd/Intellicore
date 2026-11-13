package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LayerMetadata
import com.example.ui.theme.ArtemisIcons
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LunarBorder
import com.example.ui.theme.LunarSurfaceDark
import com.example.ui.theme.LunarSurfaceElevated
import com.example.ui.theme.ScientificCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LayerMetadataChip(
    metadata: LayerMetadata,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    val badgeColor = Color(metadata.classification.badgeColorHex)

    Box(
        modifier = modifier
            .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(6.dp)
                    .background(badgeColor, RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = metadata.classification.label,
                style = MaterialTheme.typography.labelSmall,
                color = badgeColor,
                fontWeight = FontWeight.Bold,
                fontFamily = JetBrainsMonoFontFamily
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "View Layer Scientific Metadata",
                tint = badgeColor.copy(alpha = 0.8f),
                modifier = Modifier
                    .width(12.dp)
                    .height(12.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .height(8.dp)
                            .background(badgeColor, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = metadata.classification.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LunarSurfaceElevated),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        MetadataRow(label = "Source Instrument", value = metadata.sourceInstrument)
                        HorizontalDivider(color = LunarBorder, modifier = Modifier.padding(vertical = 6.dp))
                        MetadataRow(label = "Acquisition Epoch", value = metadata.acquisitionEpoch)
                        HorizontalDivider(color = LunarBorder, modifier = Modifier.padding(vertical = 6.dp))
                        MetadataRow(label = "Spatial Resolution", value = metadata.spatialResolution)
                        HorizontalDivider(color = LunarBorder, modifier = Modifier.padding(vertical = 6.dp))
                        MetadataRow(label = "Units", value = metadata.units)
                        HorizontalDivider(color = LunarBorder, modifier = Modifier.padding(vertical = 6.dp))
                        MetadataRow(label = "Processing Method", value = metadata.processingMethod)
                        HorizontalDivider(color = LunarBorder, modifier = Modifier.padding(vertical = 6.dp))
                        MetadataRow(label = "Uncertainty Margin", value = metadata.uncertainty)

                        if (!metadata.pdsUrl.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ScientificCyan.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
                                    .border(1.dp, ScientificCyan.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        runCatching { uriHandler.openUri(metadata.pdsUrl) }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = ArtemisIcons.OpenInNew,
                                    contentDescription = "Open NASA PDS Archive",
                                    tint = ScientificCyan,
                                    modifier = Modifier.width(14.dp).height(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "OFFICIAL NASA PDS ARCHIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ScientificCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                    Text(
                                        text = metadata.pdsUrl,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = LunarSurfaceDark
        )
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 9.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

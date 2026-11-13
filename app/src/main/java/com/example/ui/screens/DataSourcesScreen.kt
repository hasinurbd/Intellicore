package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataSourceClassification
import com.example.data.model.ScientificDataSource
import com.example.ui.theme.ArtemisIcons
import com.example.ui.theme.ArtemisOrange
import com.example.ui.theme.CryogenicViolet
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LunarBorder
import com.example.ui.theme.LunarDeepNavy
import com.example.ui.theme.LunarSurfaceDark
import com.example.ui.theme.LunarSurfaceElevated
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.ScientificCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DataSourcesScreen(viewModel: MainViewModel) {
    val allSources = viewModel.scientificDataSources
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    val filterCategories = remember {
        listOf("All Sources", "Measured Observations", "Derived Proxies")
    }

    val observationCount = remember(allSources) {
        allSources.count { it.classification == DataSourceClassification.MEASURED_OBSERVATION }
    }
    val proxyCount = remember(allSources) {
        allSources.count { it.classification == DataSourceClassification.DERIVED_PROXY }
    }

    val filteredSources = remember(allSources, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> allSources.filter { it.classification == DataSourceClassification.MEASURED_OBSERVATION }
            2 -> allSources.filter { it.classification == DataSourceClassification.DERIVED_PROXY }
            else -> allSources
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LunarDeepNavy)
            .padding(16.dp)
            .testTag("data_sources_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceElevated),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = ArtemisIcons.Science,
                                contentDescription = "Data Sources",
                                tint = ArtemisOrange
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DATA PROVENANCE & PDS CITATIONS",
                                style = MaterialTheme.typography.titleMedium,
                                color = ArtemisOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Traceable evidence index referencing official NASA PDS archives (LOLA, Diviner, LEND, Lunar Prospector, Clementine, Chandrayaan-1 M³). Values are bundled locally and linked to source archives.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 100% Offline / Airplane Mode banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SafeEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, SafeEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = ArtemisIcons.AirplanemodeActive,
                                contentDescription = "Airplane Mode Safe",
                                tint = SafeEmerald
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "100% OFFLINE & AIRPLANE-MODE VERIFIED",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SafeEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = JetBrainsMonoFontFamily
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = SafeEmerald,
                                        modifier = Modifier.width(14.dp)
                                    )
                                }
                                Text(
                                    text = "All orbital DEM rasters, thermal composites, neutron proxies, and simulation models are bundled locally. Tap any PDS URL below to view the source archive in your browser when online.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filter Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedFilterIndex,
                containerColor = LunarSurfaceDark,
                contentColor = ArtemisOrange,
                edgePadding = 0.dp,
                divider = { HorizontalDivider(color = LunarBorder) }
            ) {
                filterCategories.forEachIndexed { index, name ->
                    Tab(
                        selected = selectedFilterIndex == index,
                        onClick = { selectedFilterIndex = index },
                        text = {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedFilterIndex == index) ArtemisOrange else TextMuted
                            )
                        }
                    )
                }
            }
        }

        // Dynamic Summary Statistics Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(LunarSurfaceDark, RoundedCornerShape(8.dp))
                        .border(1.dp, LunarBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("OBSERVATIONS", style = MaterialTheme.typography.labelSmall, color = ScientificCyan, fontSize = 9.sp, fontFamily = JetBrainsMonoFontFamily)
                        Text("$observationCount Datasets", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("LOLA DEM, Diviner Thermal, VIPER Baseline", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 10.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(LunarSurfaceDark, RoundedCornerShape(8.dp))
                        .border(1.dp, LunarBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("DERIVED PROXIES", style = MaterialTheme.typography.labelSmall, color = CryogenicViolet, fontSize = 9.sp, fontFamily = JetBrainsMonoFontFamily)
                        Text("$proxyCount Models", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("LEND, LP-NS, Clementine, M³, LAMP", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // Source Items
        items(filteredSources, key = { it.id }) { source ->
            ScientificDataSourceCard(source = source)
        }

        // Citation Note
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, LunarBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Planetary Data System (PDS) Compliance Note",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All identifiers and URLs reference official NASA Planetary Data System standard product volumes archived at the Geosciences Node (Washington University in St. Louis) and Imaging Node (NASA JPL). Data used is free, open access public domain orbital science.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ScientificDataSourceCard(source: ScientificDataSource) {
    val isObservation = source.classification == DataSourceClassification.MEASURED_OBSERVATION
    val badgeColor = if (isObservation) ScientificCyan else CryogenicViolet
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("source_card_${source.id}"),
        colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, LunarBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Classification Badge & Agency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = source.classification.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 9.sp
                    )
                }

                Text(
                    text = source.missionAgency,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Instrument
            Text(
                text = source.title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Instrument: ${source.instrument}",
                style = MaterialTheme.typography.bodySmall,
                color = ArtemisOrange,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // PDS Product Identifier
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LunarDeepNavy, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Column {
                    Text(
                        text = "PDS PRODUCT IDENTIFIER:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                    Text(
                        text = source.pdsProductIdentifier,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                    Text(
                        text = "Archive: ${source.archiveNode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Clickable Official PDS Dataset URL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .background(ScientificCyan.copy(alpha = 0.10f), RoundedCornerShape(6.dp))
                    .border(1.dp, ScientificCyan.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                    .clickable {
                        runCatching { uriHandler.openUri(source.pdsUrl) }
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .testTag("source_link_${source.id}"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ArtemisIcons.OpenInNew,
                    contentDescription = "Open PDS Dataset Archive",
                    tint = ScientificCyan,
                    modifier = Modifier.width(16.dp).height(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "OFFICIAL PDS DATASET URL (TAP TO OPEN)",
                        style = MaterialTheme.typography.labelSmall,
                        color = ScientificCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                    Text(
                        text = source.pdsUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // What It Measures
            Row {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Measures",
                    tint = ScientificCyan,
                    modifier = Modifier.width(16.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "What It Measures:",
                        style = MaterialTheme.typography.labelSmall,
                        color = ScientificCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = source.whatItMeasures,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // What It Does NOT Prove (Scientific Transparency / Caveats)
            Row {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Caveats",
                    tint = WarningAmber,
                    modifier = Modifier.width(16.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Scientific Distinction & Caveat (What It Does NOT Prove):",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = source.whatItDoesNotProve,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = LunarBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Technical Specs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("SPATIAL RESOLUTION", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                    Text(source.spatialResolution, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontSize = 11.sp, fontFamily = JetBrainsMonoFontFamily)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("UNCERTAINTY", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                    Text(source.uncertaintyMargin, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontSize = 11.sp, fontFamily = JetBrainsMonoFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Role in Decision Making
            Text(
                text = "ROLE IN EXPLORATION DECISION:",
                style = MaterialTheme.typography.labelSmall,
                color = ArtemisOrange,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = source.roleInDecisionMaking,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Status Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Status",
                    tint = SafeEmerald,
                    modifier = Modifier.width(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = source.offlineVerificationStatus,
                    style = MaterialTheme.typography.labelSmall,
                    color = SafeEmerald,
                    fontSize = 10.sp,
                    fontFamily = JetBrainsMonoFontFamily
                )
            }
        }
    }
}

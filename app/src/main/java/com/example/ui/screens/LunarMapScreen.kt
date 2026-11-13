package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResourcePoint
import com.example.data.model.RouteWaypoint
import com.example.ui.components.LayerMetadataChip
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
import com.example.ui.theme.ScientificCyan
import com.example.ui.theme.SolarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VolatileIceAqua
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private data class PolarProjectedPoint(
    val id: String,
    val normX: Float,
    val normY: Float
)

private data class PolarCraterFeature(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusDeg: Double,
    val isPsr: Boolean
)

private data class MapSurfaceProbe(
    val latitudeDeg: Double,
    val longitudeDeg: Double,
    val distanceFromPoleKm: Double,
    val normX: Float,
    val normY: Float
)

private data class InspectedWaypoint(
    val waypoint: RouteWaypoint,
    val isProposedRoute: Boolean
)

private fun projectPolarNormalized(latitude: Double, longitude: Double): Offset {
    val latOffset = (-latitude - 80.0).coerceIn(0.0, 10.0)
    val normR = (1.0 - (latOffset / 10.0)).toFloat()
    val rad = Math.toRadians(longitude)
    return Offset((normR * cos(rad)).toFloat(), (normR * sin(rad)).toFloat())
}

private fun clampPanOffset(pan: Offset, baseRadius: Float, zoom: Float): Offset {
    val maxPan = (baseRadius * (zoom - 0.55f)).coerceAtLeast(baseRadius * 0.45f)
    return Offset(
        x = pan.x.coerceIn(-maxPan, maxPan),
        y = pan.y.coerceIn(-maxPan, maxPan)
    )
}

@Composable
fun LunarMapScreen(viewModel: MainViewModel) {
    val sites = viewModel.sites
    val selectedSite by viewModel.selectedSite.collectAsStateWithLifecycle()
    val routeComparison = viewModel.routeComparison
    val telemetry by viewModel.telemetryState.collectAsStateWithLifecycle()

    var selectedLayerIndex by remember { mutableIntStateOf(0) }
    val layerNames = remember {
        listOf(
            "LOLA Elevation",
            "Diviner Thermal",
            "Neutron Hydrogen",
            "TiO2 / Ilmenite"
        )
    }

    // Interactive map overlays & inspect state
    var showRoutes by remember { mutableStateOf(true) }
    var showCraters by remember { mutableStateOf(true) }
    var showRoverMarker by remember { mutableStateOf(true) }
    var isMapExpanded by remember { mutableStateOf(false) }
    var inspectedWaypoint by remember { mutableStateOf<InspectedWaypoint?>(null) }
    var surfaceProbe by remember { mutableStateOf<MapSurfaceProbe?>(null) }

    // Camera zoom & pan animatables
    val coroutineScope = rememberCoroutineScope()
    val zoomAnim = remember { Animatable(1f) }
    val panAnim = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val mapHeight by animateDpAsState(
        targetValue = if (isMapExpanded) 460.dp else 330.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "map_height"
    )

    // Correctly bind metadata to the active layer tab rather than the site's default resource
    val currentMetadata = remember(selectedLayerIndex) {
        viewModel.getMetadataForLayer(selectedLayerIndex)
    }

    // Precompute normalized stereographic polar projections (-80°S..90°S)
    val projectedSites = remember(sites) {
        sites.associate { site ->
            val offset = projectPolarNormalized(site.latitude, site.longitude)
            site.id to PolarProjectedPoint(site.id, offset.x, offset.y)
        }
    }

    val proposedRouteCoords = remember(routeComparison) {
        routeComparison.optimizedRoute.waypoints.map { wp ->
            projectPolarNormalized(wp.latitude, wp.longitude)
        }
    }

    val directRouteCoords = remember(routeComparison) {
        routeComparison.baselineRoute.waypoints.map { wp ->
            projectPolarNormalized(wp.latitude, wp.longitude)
        }
    }

    val polarCraters = remember {
        listOf(
            PolarCraterFeature("Shackleton", -89.90, 0.0, 0.35, true),
            PolarCraterFeature("de Gerlache", -88.50, -88.30, 0.55, true),
            PolarCraterFeature("Shoemaker", -88.10, 44.90, 0.85, true),
            PolarCraterFeature("Faustini", -87.30, 77.00, 0.68, true),
            PolarCraterFeature("Haworth", -87.50, -5.00, 0.82, true),
            PolarCraterFeature("Cabeus", -84.90, -35.50, 1.55, true),
            PolarCraterFeature("Amundsen", -84.50, 82.80, 1.65, false)
        )
    }
    val projectedCraters = remember(polarCraters) {
        polarCraters.map { it to projectPolarNormalized(it.latitude, it.longitude) }
    }
    val ringFractions = remember {
        listOf(1.0f to "80°S", 0.7f to "83°S", 0.4f to "86°S", 0.1f to "89°S")
    }
    val directPath = remember { Path() }
    val ridgePath = remember { Path() }
    val directDashEffect = remember { PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f) }
    val craterLabelStyle = remember {
        TextStyle(
            color = TextMuted.copy(alpha = 0.8f),
            fontSize = 9.sp,
            fontFamily = JetBrainsMonoFontFamily
        )
    }
    val graticuleLabelStyle = remember {
        TextStyle(
            color = TextMuted.copy(alpha = 0.75f),
            fontSize = 9.sp,
            fontFamily = JetBrainsMonoFontFamily
        )
    }
    val selectedSiteLabelStyle = remember {
        TextStyle(
            color = ArtemisOrange,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JetBrainsMonoFontFamily
        )
    }
    val unselectedSiteLabelStyle = remember {
        TextStyle(
            color = TextPrimary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = JetBrainsMonoFontFamily
        )
    }

    // Helper lambdas for camera controls
    fun focusOnSite(site: ResourcePoint, targetZoom: Float = 2.6f) {
        val norm = projectedSites[site.id] ?: return
        val baseRadius = (minOf(canvasSize.width, canvasSize.height) / 2f) * 0.84f
        if (baseRadius <= 0f) return
        val clampedZoom = targetZoom.coerceIn(1f, 6f)
        val rawPan = Offset(-norm.normX * baseRadius * clampedZoom, -norm.normY * baseRadius * clampedZoom)
        val clampedPan = clampPanOffset(rawPan, baseRadius, clampedZoom)
        coroutineScope.launch {
            launch { zoomAnim.animateTo(clampedZoom, spring(stiffness = Spring.StiffnessMediumLow)) }
            launch { panAnim.animateTo(clampedPan, spring(stiffness = Spring.StiffnessMediumLow)) }
        }
    }

    fun adjustZoom(factor: Float) {
        val baseRadius = (minOf(canvasSize.width, canvasSize.height) / 2f) * 0.84f
        if (baseRadius <= 0f) return
        val oldZoom = zoomAnim.value
        val newZoom = (oldZoom * factor).coerceIn(1f, 6f)
        val scaledPan = if (newZoom <= 1.01f) Offset.Zero else clampPanOffset(
            panAnim.value * (newZoom / oldZoom),
            baseRadius,
            newZoom
        )
        coroutineScope.launch {
            launch { zoomAnim.animateTo(newZoom, spring(stiffness = Spring.StiffnessMedium)) }
            launch { panAnim.animateTo(scaledPan, spring(stiffness = Spring.StiffnessMedium)) }
        }
    }

    fun resetCamera() {
        coroutineScope.launch {
            launch { zoomAnim.animateTo(1f, spring(stiffness = Spring.StiffnessMediumLow)) }
            launch { panAnim.animateTo(Offset.Zero, spring(stiffness = Spring.StiffnessMediumLow)) }
        }
    }

    val updatedSites by rememberUpdatedState(sites)
    val updatedShowRoutes by rememberUpdatedState(showRoutes)
    val updatedRouteComparison by rememberUpdatedState(routeComparison)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LunarDeepNavy)
            .padding(16.dp)
            .testTag("lunar_map_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Compact Operational Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceElevated),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SOUTH POLE ORBITAL MAP",
                            style = MaterialTheme.typography.titleMedium,
                            color = ArtemisOrange,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .background(ScientificCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ARCHIVED DATA",
                                style = MaterialTheme.typography.labelSmall,
                                color = ScientificCyan,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 9.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Drag to pan, pinch/double-tap to zoom, or tap markers, waypoints & grid.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Layer Switcher Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedLayerIndex,
                containerColor = LunarSurfaceDark,
                contentColor = ArtemisOrange,
                edgePadding = 0.dp,
                divider = { HorizontalDivider(color = LunarBorder) }
            ) {
                layerNames.forEachIndexed { index, name ->
                    Tab(
                        selected = selectedLayerIndex == index,
                        onClick = { selectedLayerIndex = index },
                        text = {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = if (selectedLayerIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedLayerIndex == index) ArtemisOrange else TextMuted
                            )
                        }
                    )
                }
            }
        }

        // Interactive Polar Map Viewport Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
                border = BorderStroke(1.dp, LunarBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "South Polar Stereographic (-80° to -90°S)",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "ZOOM ${String.format(Locale.US, "%.1f", zoomAnim.value)}x",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArtemisOrange,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                    }

                    // Overlay Toggle Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = showRoutes,
                            onClick = { showRoutes = !showRoutes },
                            label = { Text("Routes", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(ArtemisIcons.Route, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SafeEmerald.copy(alpha = 0.2f),
                                selectedLabelColor = SafeEmerald,
                                selectedLeadingIconColor = SafeEmerald
                            )
                        )
                        FilterChip(
                            selected = showCraters,
                            onClick = { showCraters = !showCraters },
                            label = { Text("Craters & Grid", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(ArtemisIcons.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ScientificCyan.copy(alpha = 0.2f),
                                selectedLabelColor = ScientificCyan,
                                selectedLeadingIconColor = ScientificCyan
                            )
                        )
                        FilterChip(
                            selected = showRoverMarker,
                            onClick = { showRoverMarker = !showRoverMarker },
                            label = { Text("Rover (T+${telemetry.elapsedHours.toInt()}h)", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(ArtemisIcons.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ArtemisOrange.copy(alpha = 0.2f),
                                selectedLabelColor = ArtemisOrange,
                                selectedLeadingIconColor = ArtemisOrange
                            )
                        )
                    }

                    // Interactive Gesture Canvas Viewport
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(mapHeight)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LunarDeepNavy)
                            .border(1.dp, LunarBorder, RoundedCornerShape(8.dp))
                            .clipToBounds()
                            .onSizeChanged { canvasSize = it }
                            .pointerInput(Unit) {
                                detectTransformGestures { centroid, pan, zoomChange, _ ->
                                    val w = size.width.toFloat()
                                    val h = size.height.toFloat()
                                    val centerX = w / 2f
                                    val centerY = h / 2f
                                    val baseRadius = minOf(centerX, centerY) * 0.84f
                                    if (baseRadius <= 0f) return@detectTransformGestures

                                    val oldZoom = zoomAnim.value
                                    val newZoom = (oldZoom * zoomChange).coerceIn(1f, 6f)
                                    val oldPan = panAnim.value

                                    val worldX = (centroid.x - centerX - oldPan.x) / oldZoom
                                    val worldY = (centroid.y - centerY - oldPan.y) / oldZoom

                                    val rawNewPan = Offset(
                                        x = centroid.x + pan.x - centerX - worldX * newZoom,
                                        y = centroid.y + pan.y - centerY - worldY * newZoom
                                    )
                                    val clampedPan = clampPanOffset(rawNewPan, baseRadius, newZoom)

                                    coroutineScope.launch {
                                        zoomAnim.snapTo(newZoom)
                                        panAnim.snapTo(clampedPan)
                                    }
                                }
                            }
                            .pointerInput(Unit) {
                                val siteHitRadiusPx = with(density) { 36.dp.toPx() }
                                val waypointHitRadiusPx = with(density) { 28.dp.toPx() }

                                detectTapGestures(
                                    onDoubleTap = { tapOffset ->
                                        val w = size.width.toFloat()
                                        val h = size.height.toFloat()
                                        val centerX = w / 2f
                                        val centerY = h / 2f
                                        val baseRadius = minOf(centerX, centerY) * 0.84f
                                        if (baseRadius <= 0f) return@detectTapGestures

                                        val oldZoom = zoomAnim.value
                                        val oldPan = panAnim.value
                                        if (oldZoom > 1.35f) {
                                            resetCamera()
                                        } else {
                                            val newZoom = 2.6f
                                            val worldX = (tapOffset.x - centerX - oldPan.x) / oldZoom
                                            val worldY = (tapOffset.y - centerY - oldPan.y) / oldZoom
                                            val targetPan = clampPanOffset(
                                                Offset(-worldX * newZoom, -worldY * newZoom),
                                                baseRadius,
                                                newZoom
                                            )
                                            coroutineScope.launch {
                                                launch { zoomAnim.animateTo(newZoom, spring(stiffness = Spring.StiffnessMediumLow)) }
                                                launch { panAnim.animateTo(targetPan, spring(stiffness = Spring.StiffnessMediumLow)) }
                                            }
                                        }
                                    },
                                    onTap = { tapOffset ->
                                        val w = size.width.toFloat()
                                        val h = size.height.toFloat()
                                        val centerX = w / 2f
                                        val centerY = h / 2f
                                        val baseRadius = minOf(centerX, centerY) * 0.84f
                                        if (baseRadius <= 0f) return@detectTapGestures

                                        val zoom = zoomAnim.value
                                        val pan = panAnim.value

                                        // 1. Check site marker hit
                                        var closestSite: ResourcePoint? = null
                                        var closestSiteDist = Float.MAX_VALUE
                                        updatedSites.forEach { site ->
                                            val proj = projectedSites[site.id] ?: return@forEach
                                            val sx = centerX + pan.x + proj.normX * baseRadius * zoom
                                            val sy = centerY + pan.y + proj.normY * baseRadius * zoom
                                            val dist = hypot(tapOffset.x - sx, tapOffset.y - sy)
                                            if (dist <= siteHitRadiusPx && dist < closestSiteDist) {
                                                closestSiteDist = dist
                                                closestSite = site
                                            }
                                        }

                                        if (closestSite != null) {
                                            viewModel.selectSite(closestSite!!)
                                            inspectedWaypoint = null
                                            surfaceProbe = null
                                            return@detectTapGestures
                                        }

                                        // 2. Check route waypoint hit if routes are visible
                                        if (updatedShowRoutes) {
                                            var closestWp: InspectedWaypoint? = null
                                            var closestWpDist = Float.MAX_VALUE

                                            updatedRouteComparison.optimizedRoute.waypoints.forEach { wp ->
                                                val norm = projectPolarNormalized(wp.latitude, wp.longitude)
                                                val sx = centerX + pan.x + norm.x * baseRadius * zoom
                                                val sy = centerY + pan.y + norm.y * baseRadius * zoom
                                                val dist = hypot(tapOffset.x - sx, tapOffset.y - sy)
                                                if (dist <= waypointHitRadiusPx && dist < closestWpDist) {
                                                    closestWpDist = dist
                                                    closestWp = InspectedWaypoint(wp, isProposedRoute = true)
                                                }
                                            }
                                            updatedRouteComparison.baselineRoute.waypoints.forEach { wp ->
                                                val norm = projectPolarNormalized(wp.latitude, wp.longitude)
                                                val sx = centerX + pan.x + norm.x * baseRadius * zoom
                                                val sy = centerY + pan.y + norm.y * baseRadius * zoom
                                                val dist = hypot(tapOffset.x - sx, tapOffset.y - sy)
                                                if (dist <= waypointHitRadiusPx && dist < closestWpDist) {
                                                    closestWpDist = dist
                                                    closestWp = InspectedWaypoint(wp, isProposedRoute = false)
                                                }
                                            }

                                            if (closestWp != null) {
                                                inspectedWaypoint = closestWp
                                                surfaceProbe = null
                                                return@detectTapGestures
                                            }
                                        }

                                        // 3. Inverse polar stereographic surface probe
                                        val normX = (tapOffset.x - centerX - pan.x) / (baseRadius * zoom)
                                        val normY = (tapOffset.y - centerY - pan.y) / (baseRadius * zoom)
                                        val normR = hypot(normX, normY)
                                        if (normR <= 1.15f) {
                                            val latDeg = -(90.0 - (normR.coerceIn(0f, 1.15f) * 10.0))
                                            val lonDeg = Math.toDegrees(atan2(normY.toDouble(), normX.toDouble()))
                                            val distFromPoleKm = (90.0 + latDeg).coerceAtLeast(0.0) * 30.323
                                            inspectedWaypoint = null
                                            surfaceProbe = MapSurfaceProbe(
                                                latitudeDeg = latDeg,
                                                longitudeDeg = lonDeg,
                                                distanceFromPoleKm = distFromPoleKm,
                                                normX = normX,
                                                normY = normY
                                            )
                                        }
                                    }
                                )
                            }
                            .testTag("interactive_polar_map_canvas")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val zoom = zoomAnim.value
                            val pan = panAnim.value
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val baseRadius = minOf(centerX, centerY) * 0.84f

                            fun toScreen(norm: Offset): Offset = Offset(
                                x = centerX + pan.x + norm.x * baseRadius * zoom,
                                y = centerY + pan.y + norm.y * baseRadius * zoom
                            )

                            val poleScreen = Offset(centerX + pan.x, centerY + pan.y)
                            val scaledRadius = baseRadius * zoom

                            // Subtle layer-tinted polar disc backdrop
                            val discTint = when (selectedLayerIndex) {
                                0 -> SafeEmerald.copy(alpha = 0.05f)
                                1 -> VolatileIceAqua.copy(alpha = 0.07f)
                                2 -> ScientificCyan.copy(alpha = 0.07f)
                                else -> ArtemisOrange.copy(alpha = 0.06f)
                            }
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(discTint, LunarDeepNavy),
                                    center = poleScreen,
                                    radius = scaledRadius.coerceAtLeast(1f)
                                ),
                                radius = scaledRadius,
                                center = poleScreen
                            )

                            // Layer-specific spatial field intensity auras around exploration sites
                            sites.forEach { site ->
                                val proj = projectedSites[site.id] ?: return@forEach
                                val center = toScreen(Offset(proj.normX, proj.normY))
                                drawLayerFieldAura(
                                    center = center,
                                    basePixelRadius = scaledRadius * 0.14f,
                                    site = site,
                                    layerIndex = selectedLayerIndex
                                )
                            }

                            // Polar Crater Geological Outlines
                            if (showCraters) {
                                projectedCraters.forEach { (crater, normOffset) ->
                                    val craterCenter = toScreen(normOffset)
                                    val craterRadiusPx = (crater.radiusDeg.toFloat() / 10f) * scaledRadius

                                    if (crater.isPsr) {
                                        drawCircle(
                                            color = Color.Black.copy(alpha = 0.35f),
                                            radius = craterRadiusPx,
                                            center = craterCenter
                                        )
                                    }
                                    drawCircle(
                                        color = if (crater.isPsr) CryogenicViolet.copy(alpha = 0.45f) else SolarGold.copy(alpha = 0.35f),
                                        radius = craterRadiusPx,
                                        center = craterCenter,
                                        style = Stroke(width = if (zoom > 2f) 2f else 1.2f)
                                    )
                                    if (zoom >= 1.35f || crater.name == "Shackleton" || crater.name == "Cabeus") {
                                        val labelLayout = textMeasurer.measure(
                                            text = crater.name,
                                            style = craterLabelStyle
                                        )
                                        drawText(
                                            textLayoutResult = labelLayout,
                                            topLeft = Offset(
                                                x = craterCenter.x - labelLayout.size.width / 2f,
                                                y = craterCenter.y + craterRadiusPx + 3f
                                            )
                                        )
                                    }
                                }

                                // Polar Graticule Rings (80°S, 83°S, 86°S, 89°S)
                                ringFractions.forEach { (frac, label) ->
                                    val r = scaledRadius * frac
                                    drawCircle(
                                        color = LunarBorder.copy(alpha = 0.65f),
                                        radius = r,
                                        center = poleScreen,
                                        style = Stroke(width = 1f)
                                    )
                                    if (frac == 1.0f || frac == 0.4f || zoom >= 1.6f) {
                                        val degLayout = textMeasurer.measure(
                                            text = label,
                                            style = graticuleLabelStyle
                                        )
                                        drawText(
                                            textLayoutResult = degLayout,
                                            topLeft = Offset(poleScreen.x + 4f, poleScreen.y - r + 2f)
                                        )
                                    }
                                }

                                // Meridians (0°, 90°, 180°, -90° + 45° diagonals when zoomed)
                                val stepDeg = if (zoom >= 1.8f) 45 else 90
                                for (deg in 0 until 360 step stepDeg) {
                                    val rad = Math.toRadians(deg.toDouble())
                                    val end = Offset(
                                        x = poleScreen.x + scaledRadius * cos(rad).toFloat(),
                                        y = poleScreen.y + scaledRadius * sin(rad).toFloat()
                                    )
                                    drawLine(
                                        color = LunarBorder.copy(alpha = if (deg % 90 == 0) 0.55f else 0.3f),
                                        start = poleScreen,
                                        end = end,
                                        strokeWidth = 1f
                                    )
                                }
                            } else {
                                // Minimal outer boundary + South Pole cross
                                drawCircle(
                                    color = LunarBorder,
                                    radius = scaledRadius,
                                    center = poleScreen,
                                    style = Stroke(width = 1.2f)
                                )
                                drawLine(
                                    color = LunarBorder.copy(alpha = 0.4f),
                                    start = Offset(poleScreen.x - 10f, poleScreen.y),
                                    end = Offset(poleScreen.x + 10f, poleScreen.y),
                                    strokeWidth = 1.5f
                                )
                                drawLine(
                                    color = LunarBorder.copy(alpha = 0.4f),
                                    start = Offset(poleScreen.x, poleScreen.y - 10f),
                                    end = Offset(poleScreen.x, poleScreen.y + 10f),
                                    strokeWidth = 1.5f
                                )
                            }

                            // Traverse Routes & Waypoints
                            if (showRoutes) {
                                if (directRouteCoords.size >= 2) {
                                    directPath.reset()
                                    directRouteCoords.forEachIndexed { idx, norm ->
                                        val pt = toScreen(norm)
                                        if (idx == 0) directPath.moveTo(pt.x, pt.y) else directPath.lineTo(pt.x, pt.y)
                                    }
                                    drawPath(
                                        path = directPath,
                                        color = HazardCrimson.copy(alpha = 0.9f),
                                        style = Stroke(
                                            width = if (zoom > 2f) 4.5f else 3f,
                                            pathEffect = directDashEffect
                                        )
                                    )
                                    routeComparison.baselineRoute.waypoints.forEachIndexed { idx, wp ->
                                        val pt = toScreen(directRouteCoords[idx])
                                        val isInspected = inspectedWaypoint?.let { !it.isProposedRoute && it.waypoint.stepIndex == wp.stepIndex } == true
                                        drawCircle(
                                            color = if (isInspected) Color.White else HazardCrimson,
                                            radius = if (isInspected) 7.5f else 4.5f,
                                            center = pt
                                        )
                                    }
                                }

                                if (proposedRouteCoords.size >= 2) {
                                    ridgePath.reset()
                                    proposedRouteCoords.forEachIndexed { idx, norm ->
                                        val pt = toScreen(norm)
                                        if (idx == 0) ridgePath.moveTo(pt.x, pt.y) else ridgePath.lineTo(pt.x, pt.y)
                                    }
                                    drawPath(
                                        path = ridgePath,
                                        color = SafeEmerald,
                                        style = Stroke(width = if (zoom > 2f) 5.5f else 3.8f)
                                    )
                                    routeComparison.optimizedRoute.waypoints.forEachIndexed { idx, wp ->
                                        val pt = toScreen(proposedRouteCoords[idx])
                                        val isInspected = inspectedWaypoint?.let { it.isProposedRoute && it.waypoint.stepIndex == wp.stepIndex } == true
                                        drawCircle(
                                            color = if (isInspected) Color.White else SafeEmerald,
                                            radius = if (isInspected) 8f else 5f,
                                            center = pt
                                        )
                                        if (isInspected) {
                                            drawCircle(
                                                color = ArtemisOrange,
                                                radius = 13f,
                                                center = pt,
                                                style = Stroke(width = 2.2f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Surface Tap Probe Crosshair
                            surfaceProbe?.let { probe ->
                                val pt = toScreen(Offset(probe.normX, probe.normY))
                                drawCircle(color = ScientificCyan, radius = 12f, center = pt, style = Stroke(width = 1.8f))
                                drawLine(color = ScientificCyan, start = Offset(pt.x - 16f, pt.y), end = Offset(pt.x + 16f, pt.y), strokeWidth = 1.5f)
                                drawLine(color = ScientificCyan, start = Offset(pt.x, pt.y - 16f), end = Offset(pt.x, pt.y + 16f), strokeWidth = 1.5f)
                            }

                            // Exploration Site Markers + Labels
                            sites.forEach { site ->
                                val proj = projectedSites[site.id] ?: return@forEach
                                val center = toScreen(Offset(proj.normX, proj.normY))
                                val isSelected = site.id == selectedSite.id

                                val markerColor = when (selectedLayerIndex) {
                                    0 -> if (site.slopeDegrees > 18.0) HazardCrimson else if (site.slopeDegrees <= 10.0) SafeEmerald else SolarGold
                                    1 -> if (site.maxDiurnalTempKelvin < 50.0) VolatileIceAqua else SolarGold
                                    2 -> if (site.hydrogenEnrichmentPpm > 400.0) ScientificCyan else TextMuted
                                    else -> if (site.ilmeniteMineralWtPercent > 2.0) ArtemisOrange else TextSecondary
                                }

                                if (isSelected) {
                                    drawCircle(
                                        color = ArtemisOrange.copy(alpha = 0.28f),
                                        radius = 22f,
                                        center = center
                                    )
                                    drawCircle(
                                        color = ArtemisOrange,
                                        radius = 16f,
                                        center = center,
                                        style = Stroke(width = 2.5f)
                                    )
                                }

                                drawCircle(
                                    color = if (isSelected) Color.White else markerColor,
                                    radius = if (isSelected) 9f else 6.5f,
                                    center = center
                                )
                                drawCircle(
                                    color = LunarDeepNavy,
                                    radius = if (isSelected) 9f else 6.5f,
                                    center = center,
                                    style = Stroke(width = 1.5f)
                                )

                                if (isSelected || zoom >= 2.0f) {
                                    val shortName = site.name.substringBefore(" (")
                                    val nameLayout = textMeasurer.measure(
                                        text = shortName,
                                        style = if (isSelected) selectedSiteLabelStyle else unselectedSiteLabelStyle
                                    )
                                    drawText(
                                        textLayoutResult = nameLayout,
                                        topLeft = Offset(
                                            x = center.x + 12f,
                                            y = center.y - nameLayout.size.height / 2f
                                        )
                                    )
                                }
                            }

                            // Live Simulated Rover Telemetry Marker
                            if (showRoverMarker) {
                                val roverPos = toScreen(projectPolarNormalized(telemetry.currentLat, telemetry.currentLon))
                                drawCircle(
                                    color = ArtemisOrange.copy(alpha = 0.3f),
                                    radius = 16f,
                                    center = roverPos
                                )
                                drawCircle(
                                    color = SolarGold,
                                    radius = 6f,
                                    center = roverPos
                                )
                                drawCircle(
                                    color = ArtemisOrange,
                                    radius = 10f,
                                    center = roverPos,
                                    style = Stroke(width = 2f)
                                )
                            }
                        }

                        // Top-Left Floating Scale Status Pill
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .background(LunarSurfaceDark.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                .border(1.dp, LunarBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "80°S–90°S · TAP TO INSPECT",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 9.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }

                        // Top-Right Expand/Collapse Viewport Toggle
                        IconButton(
                            onClick = { isMapExpanded = !isMapExpanded },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(48.dp)
                                .background(LunarSurfaceDark.copy(alpha = 0.88f), CircleShape)
                                .border(1.dp, LunarBorder, CircleShape)
                                .testTag("map_expand_button")
                        ) {
                            Icon(
                                imageVector = if (isMapExpanded) ArtemisIcons.FullscreenExit else ArtemisIcons.Fullscreen,
                                contentDescription = if (isMapExpanded) "Compact Map" else "Expand Map",
                                tint = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Docked Interactive Camera Control Bar (48.dp accessible targets, unobstructed map view)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LunarDeepNavy, RoundedCornerShape(8.dp))
                            .border(1.dp, LunarBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { adjustZoom(1f / 1.45f) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("map_zoom_out_button")
                            ) {
                                Icon(ArtemisIcons.Remove, contentDescription = "Zoom Out", tint = TextPrimary)
                            }
                            Text(
                                text = "${String.format(Locale.US, "%.1f", zoomAnim.value)}x",
                                style = MaterialTheme.typography.labelSmall,
                                color = ArtemisOrange,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                            IconButton(
                                onClick = { adjustZoom(1.45f) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("map_zoom_in_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TextPrimary)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { focusOnSite(selectedSite) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("map_center_site_button")
                            ) {
                                Icon(ArtemisIcons.MyLocation, contentDescription = "Center on Selected Site", tint = ArtemisOrange)
                            }
                            IconButton(
                                onClick = { resetCamera() },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("map_reset_view_button")
                            ) {
                                Icon(ArtemisIcons.RestartAlt, contentDescription = "Reset Map View", tint = ScientificCyan)
                            }
                        }
                    }

                    // Interactive Inspected Waypoint or Tapped Surface Probe Banner
                    AnimatedVisibility(visible = inspectedWaypoint != null || surfaceProbe != null) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            inspectedWaypoint?.let { info ->
                                val wp = info.waypoint
                                val accent = if (info.isProposedRoute) SafeEmerald else HazardCrimson
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(accent.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                        .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${if (info.isProposedRoute) "PROPOSED RIDGE" else "DIRECT ROUTE"} · WP ${wp.stepIndex}: ${wp.name}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = accent,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = JetBrainsMonoFontFamily
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Slope: ${wp.slopeDeg}° | Elev: ${wp.elevationM.toInt()}m | Solar: ${wp.solarIlluminationPercent.toInt()}% | Earth LOS: ${wp.earthLosPercent.toInt()}% | Dist: ${wp.cumulativeDistanceKm} km",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextPrimary,
                                                fontSize = 11.sp,
                                                fontFamily = JetBrainsMonoFontFamily
                                            )
                                        }
                                        IconButton(
                                            onClick = { inspectedWaypoint = null },
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Close waypoint info", tint = TextMuted)
                                        }
                                    }
                                }
                            }

                            surfaceProbe?.let { probe ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(ScientificCyan.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                        .border(1.dp, ScientificCyan.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "SURFACE PROBE COORDINATES",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ScientificCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = JetBrainsMonoFontFamily
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${String.format(Locale.US, "%.2f", abs(probe.latitudeDeg))}°S, ${String.format(Locale.US, "%.1f", probe.longitudeDeg)}°E · ${String.format(Locale.US, "%.1f", probe.distanceFromPoleKm)} km from South Pole",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextPrimary,
                                                fontSize = 11.sp,
                                                fontFamily = JetBrainsMonoFontFamily
                                            )
                                        }
                                        IconButton(
                                            onClick = { surfaceProbe = null },
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear coordinate probe", tint = TextMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    // Route & Polar Legend
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.width(14.dp).height(3.dp).background(SafeEmerald))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("14.8 km Proposed Ridge", style = MaterialTheme.typography.labelSmall, color = SafeEmerald, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.width(14.dp).height(3.dp).background(HazardCrimson))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("9.2 km Direct (29.4° Fail)", style = MaterialTheme.typography.labelSmall, color = HazardCrimson, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(SolarGold, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rover Pos", style = MaterialTheme.typography.labelSmall, color = SolarGold, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = LunarBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Selected Site Telemetry Summary + Quick Center Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Selected Site: ${selectedSite.name}",
                                style = MaterialTheme.typography.titleSmall,
                                color = ArtemisOrange,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val activeLayerReadout = when (selectedLayerIndex) {
                                0 -> "Slope: ${selectedSite.slopeDegrees}° | Elev: ${selectedSite.elevationMeters.toInt()}m | Roughness: ${selectedSite.surfaceRoughnessIndex}"
                                1 -> "Max Temp: ${selectedSite.maxDiurnalTempKelvin} K | Min: ${selectedSite.minDiurnalTempKelvin} K | Solar: ${selectedSite.solarIlluminationPercent.toInt()}%"
                                2 -> "Hydrogen Proxy: ${selectedSite.hydrogenEnrichmentPpm.toInt()} ppm H | Epithermal: ${selectedSite.epithermalNeutronCountRate} cps | Est WEH: ${selectedSite.estimatedWaterEquivalentIceWtPercent} wt%"
                                else -> "TiO2: ${selectedSite.titaniumDioxideWtPercent} wt% | Ilmenite (FeTiO3): ${selectedSite.ilmeniteMineralWtPercent} wt% | He-3 (Model): ${selectedSite.helium3ModelEstimatePpb} ppb"
                            }
                            Text(
                                text = "${selectedSite.latitude}°S, ${selectedSite.longitude}°E\n$activeLayerReadout",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { focusOnSite(selectedSite) },
                            border = BorderStroke(1.dp, ArtemisOrange.copy(alpha = 0.6f))
                        ) {
                            Icon(ArtemisIcons.MyLocation, contentDescription = null, tint = ArtemisOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Focus", color = ArtemisOrange, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Active Layer Scientific Metadata Chip & Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunarSurfaceDark),
                border = BorderStroke(1.dp, LunarBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Layer Rigorous Metadata",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        LayerMetadataChip(metadata = currentMetadata)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Instrument: ${currentMetadata.sourceInstrument}\n" +
                                "Resolution: ${currentMetadata.spatialResolution}\n" +
                                "Units: ${currentMetadata.units}\n" +
                                "Uncertainty: ${currentMetadata.uncertainty}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Site Selector Cards
        item {
            Text(
                text = "South Polar Exploration Catalog",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(sites, key = { it.id }) { site ->
            val isSelected = site.id == selectedSite.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.selectSite(site)
                        inspectedWaypoint = null
                        surfaceProbe = null
                        focusOnSite(site)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) LunarSurfaceElevated else LunarSurfaceDark
                ),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) ArtemisOrange else LunarBorder
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = site.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) ArtemisOrange else TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (site.isPermanentlyShadowedRegion) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(CryogenicViolet.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("PSR COLD TRAP", style = MaterialTheme.typography.labelSmall, color = CryogenicViolet, fontSize = 9.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hydrogen: ${site.hydrogenEnrichmentPpm.toInt()} ppm H (Proxy) | Epithermal: ${site.epithermalNeutronCountRate} cps (Obs) | Max Temp: ${site.maxDiurnalTempKelvin} K",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun DrawScope.drawLayerFieldAura(
    center: Offset,
    basePixelRadius: Float,
    site: ResourcePoint,
    layerIndex: Int
) {
    val safeRadius = basePixelRadius.coerceAtLeast(18f)
    val auraColor: Color
    val intensity: Float
    when (layerIndex) {
        0 -> {
            auraColor = if (site.slopeDegrees > 18.0) HazardCrimson else if (site.slopeDegrees <= 10.0) SafeEmerald else WarningAmber
            intensity = 0.22f
        }
        1 -> {
            auraColor = if (site.maxDiurnalTempKelvin < 50.0) VolatileIceAqua else SolarGold
            intensity = if (site.maxDiurnalTempKelvin < 50.0) 0.28f else 0.18f
        }
        2 -> {
            auraColor = ScientificCyan
            intensity = (site.hydrogenEnrichmentPpm / 700.0).toFloat().coerceIn(0.08f, 0.34f)
        }
        else -> {
            auraColor = ArtemisOrange
            intensity = (site.ilmeniteMineralWtPercent / 5.0).toFloat().coerceIn(0.08f, 0.32f)
        }
    }
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(auraColor.copy(alpha = intensity), Color.Transparent),
            center = center,
            radius = safeRadius
        ),
        radius = safeRadius,
        center = center
    )
}

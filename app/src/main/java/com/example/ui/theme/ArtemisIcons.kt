package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Lightweight, zero-bloat Material 24dp vector icons used across Artemis Resource Navigator.
 * Replaces the ~25 MB material-icons-extended artifact to drastically reduce APK size and cold-start DEX loading.
 */
object ArtemisIcons {
    val AirplanemodeActive: ImageVector by lazy {
        vectorIcon(
            "AirplanemodeActive",
            "M22,16v-2l-8.5,-5V3.5c0,-0.83 -0.67,-1.5 -1.5,-1.5s-1.5,0.67 -1.5,1.5V9L2,14v2l8.5,-2.5V19L8,20.5V22l4,-1 4,1v-1.5L13.5,19v-5.5L22,16z"
        )
    }
    val Assessment: ImageVector by lazy {
        vectorIcon(
            "Assessment",
            "M19,3H5c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2V5c0,-1.1 -0.9,-2 -2,-2zM9,17H7v-7h2v7zM13,17h-2V7h2v10zM17,17h-2v-4h2v4z"
        )
    }
    val Dashboard: ImageVector by lazy {
        vectorIcon(
            "Dashboard",
            "M3,13h8V3H3v10zM3,21h8v-6H3v6zM13,21h8V11h-8v10zM13,3v6h8V3h-8z"
        )
    }
    val Description: ImageVector by lazy {
        vectorIcon(
            "Description",
            "M14,2H6c-1.1,0 -1.99,0.9 -1.99,2L4,20c0,1.1 0.89,2 1.99,2H18c1.1,0 2,-0.9 2,-2V8l-6,-6zM16,18H8v-2h8v2zM16,14H8v-2h8v2zM13,9V3.5L18.5,9H13z"
        )
    }
    val Explore: ImageVector by lazy {
        vectorIcon(
            "Explore",
            "M12,10.9c-0.61,0 -1.1,0.49 -1.1,1.1s0.49,1.1 1.1,1.1c0.61,0 1.1,-0.49 1.1,-1.1s-0.49,-1.1 -1.1,-1.1zM12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM14.19,14.19L6,18l3.81,-8.19L18,6l-3.81,8.19z"
        )
    }
    val Fullscreen: ImageVector by lazy {
        vectorIcon(
            "Fullscreen",
            "M7,14H5v5h5v-2H7v-3zM5,10h2V7h3V5H5v5zM17,17h-3v2h5v-5h-2v3zM14,5v2h3v3h2V5h-5z"
        )
    }
    val FullscreenExit: ImageVector by lazy {
        vectorIcon(
            "FullscreenExit",
            "M5,16h3v3h2v-5H5v2zM8,8H5v2h5V5H8v3zM14,19h2v-3h3v-2h-5v5zM16,8V5h-2v5h5V8h-3z"
        )
    }
    val Layers: ImageVector by lazy {
        vectorIcon(
            "Layers",
            "M11.99,18.54l-7.37,-5.73L3,14.07l9,7 9,-7 -1.63,-1.27 -7.38,5.74zM12,16l7.36,-5.73L21,9l-9,-7 -9,7 1.63,1.27L12,16z"
        )
    }
    val Map: ImageVector by lazy {
        vectorIcon(
            "Map",
            "M20.5,3l-0.16,0.03L15,5.1 9,3 3.36,4.9c-0.21,0.07 -0.36,0.25 -0.36,0.48V20.5c0,0.28 0.22,0.5 0.5,0.5l0.16,-0.03L9,18.9l6,2.1 5.64,-1.9c0.21,-0.07 0.36,-0.25 0.36,-0.48V3.5c0,-0.28 -0.22,-0.5 -0.5,-0.5zM15,19l-6,-2.11V5l6,2.11V19z"
        )
    }
    val MyLocation: ImageVector by lazy {
        vectorIcon(
            "MyLocation",
            "M12,8c-2.21,0 -4,1.79 -4,4s1.79,4 4,4 4,-1.79 4,-4 -1.79,-4 -4,-4zM20.94,11c-0.46,-4.17 -3.77,-7.48 -7.94,-7.94V1h-2v2.06C6.83,3.52 3.52,6.83 3.06,11H1v2h2.06c0.46,4.17 3.77,7.48 7.94,7.94V23h2v-2.06c4.17,-0.46 7.48,-3.77 7.94,-7.94H23v-2h-2.06zM12,19c-3.87,0 -7,-3.13 -7,-7s3.13,-7 7,-7 7,3.13 7,7 -3.13,7 -7,7z"
        )
    }
    val OpenInNew: ImageVector by lazy {
        vectorIcon(
            "OpenInNew",
            "M19,19H5V5h7V3H5c-1.11,0 -2,0.9 -2,2v14c0,1.1 0.89,2 2,2h14c1.1,0 2,-0.9 2,-2v-7h-2v7zM14,3v2h3.59l-9.83,9.83 1.41,1.41L19,6.41V10h2V3h-7z",
            autoMirror = true
        )
    }
    val Pause: ImageVector by lazy {
        vectorIcon(
            "Pause",
            "M6,19h4V5H6v14zM14,5v14h4V5h-4z"
        )
    }
    val Remove: ImageVector by lazy {
        vectorIcon(
            "Remove",
            "M19,13H5v-2h14v2z"
        )
    }
    val RestartAlt: ImageVector by lazy {
        vectorIcon(
            "RestartAlt",
            "M12,5V2L8,6l4,4V7c3.31,0 6,2.69 6,6 0,2.97 -2.17,5.43 -5,5.91v2.02c3.95,-0.49 7,-3.85 7,-7.93 0,-4.42 -3.58,-8 -8,-8zM6,13c0,-1.65 0.67,-3.15 1.76,-4.24L6.34,7.34C4.9,8.79 4,10.79 4,13c0,4.08 3.05,7.44 7,7.93v-2.02c-2.83,-0.48 -5,-2.94 -5,-5.91z"
        )
    }
    val RocketLaunch: ImageVector by lazy {
        vectorIcon(
            "RocketLaunch",
            "M9.19,6.35c-2.04,2.29 -3.44,5.58 -3.57,5.89L2,10.69l4.05,-4.05c0.47,-0.47 1.15,-0.68 1.81,-0.55l1.33,0.26zM11.17,17s3.74,-1.55 5.89,-3.7c5.4,-5.4 4.5,-9.62 4.21,-10.57 -0.95,-0.3 -5.17,-1.19 -10.57,4.21C8.55,9.09 7,12.83 7,12.83L11.17,17zM17.65,14.81c-2.29,2.04 -5.58,3.44 -5.89,3.57L13.31,22l4.05,-4.05c0.47,-0.47 0.68,-1.15 0.55,-1.81l-0.26,-1.33zM9,18c0,0.83 -0.34,1.58 -0.88,2.12C6.94,21.3 2,22 2,22s0.7,-4.94 1.88,-6.12C4.42,15.34 5.17,15 6,15c1.66,0 3,1.34 3,3zM13,9c0,-1.1 0.9,-2 2,-2s2,0.9 2,2 -0.9,2 -2,2 -2,-0.9 -2,-2z"
        )
    }
    val Route: ImageVector by lazy {
        vectorIcon(
            "Route",
            "M19,15.18V7c0,-2.21 -1.79,-4 -4,-4s-4,1.79 -4,4v10c0,1.1 -0.9,2 -2,2s-2,-0.9 -2,-2V8.82C8.16,8.4 9,7.3 9,6c0,-1.66 -1.34,-3 -3,-3S3,4.34 3,6c0,1.3 0.84,2.4 2,2.82V17c0,2.21 1.79,4 4,4s4,-1.79 4,-4V7c0,-1.1 0.9,-2 2,-2s2,0.9 2,2v8.18c-1.16,0.41 -2,1.51 -2,2.82 0,1.66 1.34,3 3,3s3,-1.34 3,-3c0,-1.3 -0.84,-2.4 -2,-2.82z"
        )
    }
    val Science: ImageVector by lazy {
        vectorIcon(
            "Science",
            "M19.8,18.4L14,10.67V6.5l1.35,-1.69c0.26,-0.33 0.03,-0.81 -0.39,-0.81H9.04c-0.42,0 -0.65,0.48 -0.39,0.81L10,6.5v4.17L4.2,18.4c-0.49,0.66 -0.02,1.6 0.8,1.6h14c0.82,0 1.29,-0.94 0.8,-1.6z"
        )
    }
    val Thermostat: ImageVector by lazy {
        vectorIcon(
            "Thermostat",
            "M15,13V5c0,-1.66 -1.34,-3 -3,-3S9,3.34 9,5v8c-1.21,0.91 -2,2.37 -2,4 0,2.76 2.24,5 5,5s5,-2.24 5,-5c0,-1.63 -0.79,-3.09 -2,-4zM11,5c0,-0.55 0.45,-1 1,-1s1,0.45 1,1h-1v1h1v2h-1v1h1v2h-2V5z"
        )
    }
    val Tune: ImageVector by lazy {
        vectorIcon(
            "Tune",
            "M3,17v2h6v-2H3zM3,5v2h10V5H3zM13,21v-2h8v-2h-8v-2h-2v6h2zM7,9v2H3v2h4v2h2V9H7zM21,13v-2H11v2h10zM15,9h2V7h4V5h-4V3h-2v6z"
        )
    }
    val WaterDrop: ImageVector by lazy {
        vectorIcon(
            "WaterDrop",
            "M12,2c-5.33,4.55 -8,8.48 -8,11.8 0,4.98 3.8,8.2 8,8.2s8,-3.22 8,-8.2c0,-3.32 -2.67,-7.25 -8,-11.8z"
        )
    }

    private fun vectorIcon(
        name: String,
        pathData: String,
        autoMirror: Boolean = false
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = autoMirror
    ).apply {
        addPath(
            pathData = addPathNodes(pathData),
            fill = SolidColor(Color.Black)
        )
    }.build()
}

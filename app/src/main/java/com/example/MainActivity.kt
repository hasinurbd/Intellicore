package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DataSourcesScreen
import com.example.ui.screens.FocusMissionScreen
import com.example.ui.screens.LunarMapScreen
import com.example.ui.screens.MiningIndexScreen
import com.example.ui.screens.RoadmapScreen
import com.example.ui.screens.RoverRouteScreen
import com.example.ui.theme.ArtemisIcons
import com.example.ui.theme.ArtemisNavigatorTheme
import com.example.ui.theme.ArtemisOrange
import com.example.ui.theme.LunarDeepNavy
import com.example.ui.theme.LunarSurfaceDark
import com.example.ui.theme.ScientificCyan
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ArtemisNavigatorTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    // 0: Dashboard, 1: Focus Mission, 2: Map, 3: MVI, 4: Traverse, 5: Sources, 6: Roadmap
    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    // Android back handler: Return to Dashboard if on a sub-screen
    BackHandler(enabled = currentTab != 0) {
        currentTab = 0
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(), // Ensures all screens start cleanly beneath the status bar
        bottomBar = {
            NavigationBar(
                containerColor = LunarSurfaceDark,
                contentColor = ArtemisOrange,
                tonalElevation = 4.dp
            ) {
                // 1. Overview
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(imageVector = ArtemisIcons.Dashboard, contentDescription = "Overview") },
                    label = { Text("Overview", fontSize = 10.sp, maxLines = 1) },
                    alwaysShowLabel = true,
                    modifier = Modifier.testTag("nav_dashboard"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArtemisOrange,
                        selectedTextColor = ArtemisOrange,
                        indicatorColor = ArtemisOrange.copy(alpha = 0.2f),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )

                // 2. Mission (single-line label, prevents two-line wrap)
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(imageVector = ArtemisIcons.Explore, contentDescription = "Focus Mission") },
                    label = { Text("Mission", fontSize = 10.sp, maxLines = 1) },
                    alwaysShowLabel = true,
                    modifier = Modifier.testTag("nav_focus_mission"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArtemisOrange,
                        selectedTextColor = ArtemisOrange,
                        indicatorColor = ArtemisOrange.copy(alpha = 0.2f),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )

                // 3. Map (clean single-line label)
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(imageVector = ArtemisIcons.Map, contentDescription = "Orbital Map") },
                    label = { Text("Map", fontSize = 10.sp, maxLines = 1) },
                    alwaysShowLabel = true,
                    modifier = Modifier.testTag("nav_map"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArtemisOrange,
                        selectedTextColor = ArtemisOrange,
                        indicatorColor = ArtemisOrange.copy(alpha = 0.2f),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )

                // 4. MVI (clean single-line label)
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(imageVector = ArtemisIcons.Assessment, contentDescription = "MVI Index") },
                    label = { Text("MVI", fontSize = 10.sp, maxLines = 1) },
                    alwaysShowLabel = true,
                    modifier = Modifier.testTag("nav_mvi"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArtemisOrange,
                        selectedTextColor = ArtemisOrange,
                        indicatorColor = ArtemisOrange.copy(alpha = 0.2f),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )

                // 5. Traverse
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { currentTab = 4 },
                    icon = { Icon(imageVector = ArtemisIcons.Route, contentDescription = "Rover Route") },
                    label = { Text("Traverse", fontSize = 10.sp, maxLines = 1) },
                    alwaysShowLabel = true,
                    modifier = Modifier.testTag("nav_route"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArtemisOrange,
                        selectedTextColor = ArtemisOrange,
                        indicatorColor = ArtemisOrange.copy(alpha = 0.2f),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )

                // 6. Sources
                NavigationBarItem(
                    selected = currentTab == 5,
                    onClick = { currentTab = 5 },
                    icon = { Icon(imageVector = ArtemisIcons.Science, contentDescription = "Data Sources") },
                    label = { Text("Sources", fontSize = 10.sp, maxLines = 1) },
                    alwaysShowLabel = true,
                    modifier = Modifier.testTag("nav_sources"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ScientificCyan,
                        selectedTextColor = ScientificCyan,
                        indicatorColor = ScientificCyan.copy(alpha = 0.2f),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LunarDeepNavy)
        ) {
            saveableStateHolder.SaveableStateProvider(key = currentTab) {
                when (currentTab) {
                    0 -> DashboardScreen(
                        onNavigateToFocusMission = { currentTab = 1 },
                        onNavigateToMap = { currentTab = 2 },
                        onNavigateToMvi = { currentTab = 3 },
                        onNavigateToRoute = { currentTab = 4 },
                        onNavigateToRoadmap = { currentTab = 6 },
                        onNavigateToSources = { currentTab = 5 }
                    )
                    1 -> FocusMissionScreen(
                        viewModel = viewModel,
                        onNavigateToSources = { currentTab = 5 }
                    )
                    2 -> LunarMapScreen(viewModel = viewModel)
                    3 -> MiningIndexScreen(viewModel = viewModel)
                    4 -> RoverRouteScreen(viewModel = viewModel)
                    5 -> DataSourcesScreen(viewModel = viewModel)
                    6 -> RoadmapScreen()
                }
            }
        }
    }
}

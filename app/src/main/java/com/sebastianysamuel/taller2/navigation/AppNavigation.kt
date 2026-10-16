package com.sebastianysamuel.taller2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sebastianysamuel.taller2.ui.home.HomeScreen
import com.sebastianysamuel.taller2.ui.map.MapScreen
import com.sebastianysamuel.taller2.ui.media.MediaScreen

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(HomeRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(
                    onOpenMedia = { backStack.add(MediaRoute) },
                    onOpenMap = { backStack.add(MapRoute) },
                )
            }
            entry<MediaRoute> {
                MediaScreen(onBack = { backStack.removeLastOrNull() })
            }
            entry<MapRoute> {
                MapScreen(onBack = { backStack.removeLastOrNull() })
            }
        },
    )
}

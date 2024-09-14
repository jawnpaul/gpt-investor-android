package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thejawnpaul.gptinvestor.core.navigation.Screen

/*@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.HomeTabScreen to Icons.Default.Home,
        Screen.DiscoverTabScreen to Icons.Default.Search,
        Screen.HistoryTabScreen to Icons.Default.Menu
    )

    BottomAppBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        if (items.any { it.first.route == currentRoute }) {
            items.forEach { (screen, icon) ->
                NavigationBarItem(
                    icon = { Icon(icon, contentDescription = screen.route) },
                    label = { Text(screen.route) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}*/

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.HomeTabScreen to Icons.Default.Home,
        Screen.DiscoverTabScreen to Icons.Default.Search,
        Screen.HistoryTabScreen to Icons.Default.Menu
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (items.any { it.first.route == currentRoute }) {
        NavigationBar(containerColor = Color.Transparent) {
            items.forEach { (screen, icon) ->
                NavigationBarItem(
                    icon = { Icon(icon, contentDescription = screen.route) },
                    label = { Text(screen.route.replace("_tab_screen", "").capitalize()) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thejawnpaul.gptinvestor.R
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
        Screen.HomeTabScreen to ImageVector.vectorResource(R.drawable.ask_ai_icon),
        Screen.DiscoverTabScreen to ImageVector.vectorResource(R.drawable.discover_icon),
        Screen.HistoryTabScreen to ImageVector.vectorResource(R.drawable.history_icon_2)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider()
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
                        },
                        colors = NavigationBarItemDefaults.colors().copy(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            selectedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

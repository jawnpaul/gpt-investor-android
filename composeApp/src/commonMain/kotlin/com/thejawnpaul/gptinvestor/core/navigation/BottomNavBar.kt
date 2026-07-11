package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.ic_book
import com.thejawnpaul.gptinvestor.ic_book_filled
import com.thejawnpaul.gptinvestor.ic_discover
import com.thejawnpaul.gptinvestor.ic_discover_filled
import com.thejawnpaul.gptinvestor.ic_home_filled
import com.thejawnpaul.gptinvestor.ic_home_trend_up
import com.thejawnpaul.gptinvestor.ic_profile
import com.thejawnpaul.gptinvestor.ic_profile_filled
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    val items = listOf(
        BottomNavItem(
            screen = Screen.HomeTabScreen,
            label = "Home",
            icons = NavigationIcons(
                selected = vectorResource(Res.drawable.ic_home_filled),
                unselected = vectorResource(Res.drawable.ic_home_trend_up)
            )
        ),
        BottomNavItem(
            screen = Screen.DiscoverTabScreen,
            label = "Discover",
            icons = NavigationIcons(
                selected = vectorResource(Res.drawable.ic_discover_filled),
                unselected = vectorResource(Res.drawable.ic_discover)
            )
        ),
        BottomNavItem(
            screen = Screen.WatchlistTabScreen,
            label = "Watchlist",
            icons = NavigationIcons(
                selected = vectorResource(Res.drawable.ic_book_filled),
                unselected = vectorResource(Res.drawable.ic_book)
            )
        ),
        BottomNavItem(
            screen = Screen.ProfileTabScreen,
            label = "Profile",
            icons = NavigationIcons(
                selected = vectorResource(Res.drawable.ic_profile_filled),
                unselected = vectorResource(Res.drawable.ic_profile)
            )
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column(modifier = modifier.fillMaxWidth()) {
        if (items.any { it.screen.route == currentRoute }) {
            NavigationBar(
                containerColor = Color.Transparent
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) item.icons.selected else item.icons.unselected,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Column {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.navigationRoute()) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors().copy(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            selectedIndicatorColor = Color.Transparent,
                            unselectedIconColor = gptInvestorColors.textColors.secondary50,
                            unselectedTextColor = gptInvestorColors.textColors.secondary50
                        )
                    )
                }
            }
        }
    }
}

private data class BottomNavItem(val screen: Screen, val label: String, val icons: NavigationIcons)

private data class NavigationIcons(val selected: ImageVector, val unselected: ImageVector)

private fun BottomNavItem.navigationRoute(): String =
    if (screen == Screen.DiscoverTabScreen) Screen.DiscoverTabScreen.createRoute() else screen.route

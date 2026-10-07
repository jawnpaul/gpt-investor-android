package com.thejawnpaul.gptinvestor.core.navigation

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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.discover
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import com.thejawnpaul.gptinvestor.home
import com.thejawnpaul.gptinvestor.ic_discover
import com.thejawnpaul.gptinvestor.ic_discover_filled
import com.thejawnpaul.gptinvestor.ic_home_filled
import com.thejawnpaul.gptinvestor.ic_home_trend_up
import com.thejawnpaul.gptinvestor.ic_profile
import com.thejawnpaul.gptinvestor.ic_profile_filled
import com.thejawnpaul.gptinvestor.profile
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private data class BottomNavItem(
    val label: StringResource,
    val icon: DrawableResource,
    val selectedIcon: DrawableResource,
    val route: String,
    val guestRoute: String,
    val matchingRoutes: Set<String>
)

private val bottomNavItems = listOf(
    BottomNavItem(
        label = Res.string.home,
        icon = Res.drawable.ic_home_trend_up,
        selectedIcon = Res.drawable.ic_home_filled,
        route = Screen.HomeTabScreen.route,
        guestRoute = GuestScreen.GuestHomeTab.route,
        matchingRoutes = setOf(Screen.HomeTabScreen.route, GuestScreen.GuestHomeTab.route)
    ),
    BottomNavItem(
        label = Res.string.discover,
        icon = Res.drawable.ic_discover,
        selectedIcon = Res.drawable.ic_discover_filled,
        route = Screen.DiscoverTabScreen.createRoute(),
        guestRoute = GuestScreen.GuestDiscoverTab.createRoute(),
        matchingRoutes = setOf(Screen.DiscoverTabScreen.route, GuestScreen.GuestDiscoverTab.route)
    ),
    BottomNavItem(
        label = Res.string.profile,
        icon = Res.drawable.ic_profile,
        selectedIcon = Res.drawable.ic_profile_filled,
        route = Screen.ProfileTabScreen.route,
        guestRoute = GuestScreen.GuestProfileTab.route,
        matchingRoutes = setOf(Screen.ProfileTabScreen.route, GuestScreen.GuestProfileTab.route)
    )
)

@Composable
fun BottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (bottomNavItems.none { currentRoute in it.matchingRoutes }) return

    val isGuest = currentRoute?.startsWith("guest_") == true

    NavigationBar(modifier = modifier, containerColor = Color.Transparent) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute in item.matchingRoutes
            val label = stringResource(item.label)
            NavigationBarItem(
                icon = {
                    Icon(
                        vectorResource(if (selected) item.selectedIcon else item.icon),
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(if (isGuest) item.guestRoute else item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
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

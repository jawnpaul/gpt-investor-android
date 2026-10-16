package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mohamedrejeb.calf.sf.symbols.SFSymbol
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import com.mohamedrejeb.calf.ui.navigation.AdaptiveNavigationBar
import com.mohamedrejeb.calf.ui.navigation.UIKitTabBarConfiguration
import com.mohamedrejeb.calf.ui.navigation.UIKitUITabBarItem
import com.mohamedrejeb.calf.ui.uikit.UIKitImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.core.platform.isIos
import com.thejawnpaul.gptinvestor.discover
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import com.thejawnpaul.gptinvestor.home
import com.thejawnpaul.gptinvestor.profile
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBottomNav
import com.thejawnpaul.gptinvestor.theme.components.GiBottomNavItem
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.watchlist_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private data class BottomNavItem(
    val label: StringResource,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val iosSymbol: String,
    val iosSelectedSymbol: String,
    val route: String,
    val guestRoute: String,
    val matchingRoutes: Set<String>
)

private val bottomNavItems = listOf(
    BottomNavItem(
        label = Res.string.home,
        icon = GiIcons.house,
        selectedIcon = GiIcons.houseFill,
        iosSymbol = SFSymbol.house,
        iosSelectedSymbol = SFSymbol.houseFill,
        route = Screen.HomeTabScreen.route,
        guestRoute = GuestScreen.GuestHomeTab.route,
        matchingRoutes = setOf(Screen.HomeTabScreen.route, GuestScreen.GuestHomeTab.route)
    ),
    BottomNavItem(
        label = Res.string.discover,
        icon = GiIcons.compass,
        selectedIcon = GiIcons.compassFill,
        iosSymbol = SFSymbol.safari,
        iosSelectedSymbol = SFSymbol.safariFill,
        route = Screen.DiscoverTabScreen.route,
        guestRoute = GuestScreen.GuestDiscoverTab.createRoute(),
        matchingRoutes = setOf(Screen.DiscoverTabScreen.route, GuestScreen.GuestDiscoverTab.route)
    ),
    BottomNavItem(
        label = Res.string.watchlist_title,
        icon = GiIcons.star,
        selectedIcon = GiIcons.starFill,
        iosSymbol = SFSymbol.book,
        iosSelectedSymbol = SFSymbol.bookFill,
        route = Screen.WatchlistTabScreen.route,
        guestRoute = GuestScreen.GuestWatchlistTab.route,
        matchingRoutes = setOf(Screen.WatchlistTabScreen.route, GuestScreen.GuestWatchlistTab.route)
    ),
    BottomNavItem(
        label = Res.string.profile,
        icon = GiIcons.user,
        selectedIcon = GiIcons.userFill,
        iosSymbol = SFSymbol.person,
        iosSelectedSymbol = SFSymbol.personFill,
        route = Screen.ProfileTabScreen.route,
        guestRoute = GuestScreen.GuestProfileTab.route,
        matchingRoutes = setOf(Screen.ProfileTabScreen.route, GuestScreen.GuestProfileTab.route)
    )
)

internal fun NavController.navigateToTab(route: String, homeRoute: String) {
    navigate(route) {
        popUpTo(homeRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun isBottomTabRoute(route: String?) = bottomNavItems.any { route in it.matchingRoutes }

@OptIn(ExperimentalCalfUiApi::class)
@Composable
fun BottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val routeIndex = bottomNavItems.indexOfFirst { currentRoute in it.matchingRoutes }
    var lastTab by remember { mutableStateOf(NavTab(index = routeIndex, isGuest = currentRoute.isGuestRoute())) }
    val shownTab = if (routeIndex >= 0) NavTab(index = routeIndex, isGuest = currentRoute.isGuestRoute()) else lastTab
    SideEffect { lastTab = shownTab }

    val reducedMotion = isReducedMotionEnabled()
    AnimatedVisibility(
        visible = routeIndex >= 0,
        modifier = modifier,
        enter = if (reducedMotion) {
            EnterTransition.None
        } else {
            expandVertically(GiMotion.smooth(), expandFrom = Alignment.Top) +
                slideInVertically(GiMotion.smooth()) { it }
        },
        exit = if (reducedMotion) {
            ExitTransition.None
        } else {
            shrinkVertically(GiMotion.smooth(), shrinkTowards = Alignment.Top) +
                slideOutVertically(GiMotion.smooth()) { it }
        }
    ) {
        BottomNavBarContent(navController = navController, tab = shownTab)
    }
}

@Composable
private fun BottomNavBarContent(navController: NavController, tab: NavTab, modifier: Modifier = Modifier) {
    val isGuest = tab.isGuest
    val labels = bottomNavItems.map { stringResource(it.label) }
    val selectedIndex = tab.index

    val navigateTo: (BottomNavItem) -> Unit = { item ->
        val homeRoute = if (isGuest) GuestScreen.GuestHomeTab.route else Screen.HomeTabScreen.route
        navController.navigateToTab(if (isGuest) item.guestRoute else item.route, homeRoute)
    }

    // Only the shell adapts per platform: iOS keeps the native tab bar, Android gets the v4 bar.
    if (isIos) {
        AdaptiveNavigationBar(
            modifier = modifier,
            iosItems = bottomNavItems.mapIndexed { index, item ->
                UIKitUITabBarItem(
                    title = labels[index],
                    image = UIKitImage.SystemName(item.iosSymbol),
                    selectedImage = UIKitImage.SystemName(item.iosSelectedSymbol)
                )
            },
            iosSelectedIndex = selectedIndex,
            iosOnItemSelected = { navigateTo(bottomNavItems[it]) },
            iosConfiguration = UIKitTabBarConfiguration(
                selectedItemColor = GiTheme.colors.violet,
                unselectedItemColor = GiTheme.colors.muted
            )
        ) {}
    } else {
        GiBottomNav(modifier = modifier) {
            bottomNavItems.forEachIndexed { index, item ->
                GiBottomNavItem(
                    label = labels[index],
                    icon = item.icon,
                    selectedIcon = item.selectedIcon,
                    selected = index == selectedIndex,
                    onClick = { navigateTo(item) }
                )
            }
        }
    }
}

private data class NavTab(val index: Int, val isGuest: Boolean)

private fun String?.isGuestRoute(): Boolean = this?.startsWith("guest_") == true

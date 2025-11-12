package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.history_icon_2
import gptinvestor.app.generated.resources.ic_home_trend_up
import gptinvestor.app.generated.resources.ic_search_status
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomNavBar(navController: NavController) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    val items = listOf(
        HomeTabScreen to Res.drawable.ic_home_trend_up,
        DiscoverTabScreen to Res.drawable.ic_search_status,
        HistoryTabScreen to Res.drawable.history_icon_2
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.toRoute<NavBarScreen>()

    Column(modifier = Modifier.fillMaxWidth()) {
        if (items.any { it.first == currentRoute }) {
            NavigationBar(containerColor = Color.Transparent) {
                items.forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(icon),
                                contentDescription = screen::class.simpleName
                            )
                        },
                        label = {
                            Column {
                                Text(
                                    text = screen.title.replace("_tab_screen", "")
                                        .replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (currentRoute == screen) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(top = 4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.onSurface)
                                            .size(24.dp, 4.dp)
                                    )
                                }
                            }
                        },
                        selected = currentRoute == screen,
                        onClick = {
                            navController.navigate(screen) {
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

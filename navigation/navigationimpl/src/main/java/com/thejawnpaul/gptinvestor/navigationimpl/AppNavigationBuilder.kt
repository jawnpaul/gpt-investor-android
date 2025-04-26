package com.thejawnpaul.gptinvestor.navigationimpl

import android.icu.number.Scale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.navigation.Page
import com.thejawnpaul.gptinvestor.navigation.args
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class AppNavigationBuilder @Inject constructor(
    private val navState: NavState, private val pages: Set<@JvmSuppressWildcards Page>
) {

    @Composable
    fun build(startRoute: String = "home_tab_screen", navHostController: NavHostController) {
        val navPages = remember(pages) { pages.toImmutableList() }

        NavHost(navController = navHostController, startDestination = startRoute) {
            navPages.forEach { page ->
                composable(
                    route = page.route.path,
                    arguments = page.route.args.map { arg ->
                        navArgument(arg) {
                            type = NavType.StringType
                            nullable = true
                        }
                    }
                ) { backStackEntry ->
                    page.content(backStackEntry.arguments ?: bundleOf())
                }
            }
        }

        LaunchedEffect(navState, navHostController) {
            navState.events.collect { event ->
                when (event) {
                    NavEvent.NavigateUp -> {
                        navHostController.navigateUp()
                    }

                    is NavEvent.ToRoute -> {
                        navHostController.navigate(event.route)
                    }
                }

            }
        }
    }
}
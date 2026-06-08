package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsAction
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsScreen
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    composable(route = Screen.SettingsScreen.route) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state = viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    SettingsAction.OnGoBack -> {
                        navController.navigateUp()
                    }
                }
            }.launchIn(scope)
        }

        SettingsScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent,
            onAction = viewModel::processAction
        )
    }
}

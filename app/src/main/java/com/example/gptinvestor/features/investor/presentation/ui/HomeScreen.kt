package com.example.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import timber.log.Timber

@Composable
fun HomeScreen(modifier: Modifier, navController: NavHostController, viewModel: HomeViewModel) {

    var showLandingScreen by remember { mutableStateOf(true) }
    if (showLandingScreen) {
        LandingScreen(onTimeout = { showLandingScreen = false })
    } else {
        //Home Screen
        Box(modifier = Modifier.padding(horizontal = 8.dp)){

            val state = viewModel.allSector.collectAsState()
            //row of sectors
            SectorChoiceQuestion(
                possibleAnswers = state.value.sectors,
                selectedAnswer = state.value.selected,
                onOptionSelected = {
                    Timber.e("Selected")
                    viewModel.selectSector(it)
                })
            //list of companies
        }


    }
}
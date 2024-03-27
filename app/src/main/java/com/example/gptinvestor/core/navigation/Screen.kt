package com.example.gptinvestor.core.navigation

sealed class Screen (val route: String){
    data object HomeScreen: Screen("home_screen")
}
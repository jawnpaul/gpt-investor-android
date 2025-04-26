package com.thejawnpaul.gptinvestor.navigationimpl

import kotlinx.coroutines.flow.Flow

interface NavState{

    val events: Flow<NavEvent>
}

sealed class NavEvent{
    class ToRoute(val route: String): NavEvent()

    data object NavigateUp: NavEvent()
}
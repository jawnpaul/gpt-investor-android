package com.thejawnpaul.gptinvestor.navigationimpl

import com.thejawnpaul.gptinvestor.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppNavigator @Inject constructor() : Navigator, NavState {

    private val _events = MutableSharedFlow<NavEvent>(extraBufferCapacity = 1)
    override val events: Flow<NavEvent> = _events
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun navigate(route: String) {
        coroutineScope.launch { _events.emit(NavEvent.ToRoute(route)) }
    }

    override fun navigateUp() {
        coroutineScope.launch { _events.emit(NavEvent.NavigateUp) }
    }
}
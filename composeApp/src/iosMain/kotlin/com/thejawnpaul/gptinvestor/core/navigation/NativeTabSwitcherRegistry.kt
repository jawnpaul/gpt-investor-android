package com.thejawnpaul.gptinvestor.core.navigation

interface NativeTabSwitchHandler {
    fun switchToTab(route: String)
}

object NativeTabSwitcherRegistry {
    var handler: NativeTabSwitchHandler? = null

    fun switchTo(route: String) {
        handler?.switchToTab(route)
    }
}

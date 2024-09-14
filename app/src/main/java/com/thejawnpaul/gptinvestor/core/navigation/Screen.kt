package com.thejawnpaul.gptinvestor.core.navigation

sealed class Screen(val route: String, val isTopLevel: Boolean) {
    data object HomeScreen : Screen("home_screen", false)
    data object CompanyDetailScreen : Screen("company_detail_screen", false)
    data object WebViewScreen : Screen("web_view_screen", false)

    data object HomeTabScreen : Screen("home_tab_screen", true)
    data object DiscoverTabScreen : Screen("discover_tab_screen", true)
    data object HistoryTabScreen : Screen("history_tab_screen", true)
}

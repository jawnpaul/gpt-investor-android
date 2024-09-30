package com.thejawnpaul.gptinvestor.core.navigation

sealed class Screen(val route: String, val isTopLevel: Boolean) {
    data object HomeScreen : Screen("home_screen", false)
    data object CompanyDetailScreen : Screen("company_detail_screen", false)
    data object WebViewScreen : Screen("web_view_screen/{url}", false){
        fun createRoute(url: String):String{
            return "web_view_screen/$url"
        }
    }
    data object ConversationScreen : Screen("conversation_screen", false)

    data object HomeTabScreen : Screen("Ask AI_tab_screen", true)
    data object DiscoverTabScreen : Screen("discover_tab_screen", true)
    data object HistoryTabScreen : Screen("history_tab_screen", true)
}

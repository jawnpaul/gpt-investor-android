package com.thejawnpaul.gptinvestor.core.navigation

sealed class Screen(val route: String, val isTopLevel: Boolean) {
    data object HomeScreen : Screen("home_screen", false)
    data object CompanyDetailScreen : Screen("company_detail_screen/{ticker}", false) {
        fun createRoute(ticker: String) = "company_detail_screen/$ticker"
    }

    data object WebViewScreen : Screen("web_view_screen/{url}", false) {
        fun createRoute(url: String): String {
            return "web_view_screen/$url"
        }
    }

    data object HistoryDetailScreen : Screen("history_detail_screen/{conversationId}", false) {
        fun createRoute(conversationId: Long) = "history_detail_screen/$conversationId"
    }

    data object TopPickDetailScreen : Screen("top_pick_detail_screen/{topPickId}", false) {
        fun createRoute(topPickId: String) = "top_pick_detail_screen/$topPickId"
    }

    data object AllTopPicksScreen : Screen("all_top_picks_screen", false)

    data object SavedTopPicksScreen : Screen("saved_top_picks", false)

    data object SettingsScreen : Screen("settings_screen", false)

    data object AuthenticationScreen : Screen("authentication_screen", false)

    data object ConversationScreen : Screen("conversation_screen/{chatInput}", false) {
        fun createRoute(chatInput: String) = "conversation_screen/$chatInput"
    }

    data object HomeTabScreen : Screen("home_tab_screen", true)
    data object DiscoverTabScreen : Screen("discover_tab_screen", true)
    data object HistoryTabScreen : Screen("history_tab_screen", true)
}

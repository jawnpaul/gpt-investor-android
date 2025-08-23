package com.thejawnpaul.gptinvestor.core.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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

    data object ConversationScreen :
        Screen("conversation_screen?chatInput={chatInput}&title={title}", false) {

        fun createRoute(chatInput: String, title: String? = null): String {
            val params = mutableListOf<String>()

            if (chatInput.isNotEmpty()) {
                params.add("chatInput=${URLEncoder.encode(chatInput, StandardCharsets.UTF_8.toString())}")
            }

            if (title != null) {
                params.add("title=${URLEncoder.encode(title, StandardCharsets.UTF_8.toString())}")
            }

            return if (params.isEmpty()) {
                "conversation_screen"
            } else {
                "conversation_screen?${params.joinToString("&")}"
            }
        }
    }

    data object HomeTabScreen : Screen("home_tab_screen", true)
    data object DiscoverTabScreen : Screen("discover_tab_screen", true) {
        const val deepLink = "app://gpt-investor/discover_tab_screen"
    }
    data object HistoryTabScreen : Screen("history_tab_screen", true)

    data object TidbitDetailScreen : Screen("tidbit_detail_screen/{tidbitId}", false) {
        fun createRoute(tidbitId: String) = "tidbit_detail_screen/$tidbitId"
    }
}

package com.thejawnpaul.gptinvestor.features.guest.presentation

import io.ktor.http.encodeURLParameter

sealed class GuestScreen(val route: String) {
    data object GuestHomeTab : GuestScreen("guest_home_tab_screen")

    data object GuestDiscoverTab : GuestScreen("guest_discover_tab_screen?sector={sector}") {
        fun createRoute(sectorKey: String? = null) = if (sectorKey != null) {
            "guest_discover_tab_screen?sector=$sectorKey"
        } else {
            "guest_discover_tab_screen"
        }
    }

    data object GuestHistoryTab : GuestScreen("guest_history_tab_screen")

    data object GuestConversation : GuestScreen("guest_conversation_screen?chatInput={chatInput}&title={title}") {
        fun createRoute(chatInput: String, title: String? = null): String {
            val params = mutableListOf<String>()
            if (chatInput.isNotEmpty()) params.add("chatInput=${chatInput.encodeURLParameter()}")
            if (title != null) params.add("title=${title.encodeURLParameter()}")
            return if (params.isEmpty()) {
                "guest_conversation_screen"
            } else {
                "guest_conversation_screen?${params.joinToString("&")}"
            }
        }
    }

    data object GuestHistoryDetail : GuestScreen("guest_history_detail_screen/{conversationId}") {
        fun createRoute(conversationId: Long) = "guest_history_detail_screen/$conversationId"
    }

    data object GuestCompanyDetail : GuestScreen("guest_company_detail_screen/{ticker}") {
        fun createRoute(ticker: String) = "guest_company_detail_screen/$ticker"
    }

    data object GuestTopPickDetail : GuestScreen("guest_top_pick_detail_screen/{topPickId}") {
        fun createRoute(topPickId: String) = "guest_top_pick_detail_screen/$topPickId"
    }

    data object GuestAllTopPicks : GuestScreen("guest_all_top_picks_screen")

    data object GuestSavedTopPicks : GuestScreen("guest_saved_top_picks_screen")

    data object GuestTidbitDetail : GuestScreen("guest_tidbit_detail_screen/{tidbitId}") {
        fun createRoute(tidbitId: String) = "guest_tidbit_detail_screen/$tidbitId"
    }

    data object GuestTidbitScreen : GuestScreen("guest_tidbit_screen")

    data object GuestSavedTidbitScreen : GuestScreen("guest_saved_tidbits_screen")

    data object GuestSearch : GuestScreen("guest_search_screen")
}

package com.thejawnpaul.gptinvestor.core.navigation

import kotlinx.serialization.Serializable

interface Screen

interface NavBarScreen : Screen {
    val title: String
}
@Serializable
data object HomeScreen : Screen

@Serializable
data class CompanyDetailScreen(val ticker: String) : Screen

@Serializable
data class WebViewScreen(val url: String) : Screen

@Serializable
data class HistoryDetailScreen(val conversationId: Long) : Screen

@Serializable
data class TopPickDetailScreen(val topPickId: String) : Screen

@Serializable
data object AllTopPicksScreen : Screen

@Serializable
data object SavedTopPicksScreen : Screen

@Serializable
data object SettingsScreen : Screen

@Serializable
data object AuthenticationScreen : Screen

@Serializable
data class ConversationScreen(val chatInput: String, val title: String? = null) : Screen

@Serializable
data object HomeTabScreen : NavBarScreen {
    override val title: String = "Home"
}

@Serializable
data object DiscoverTabScreen : NavBarScreen {
    override val title: String = "Discover"
    const val deepLink = "app://gpt-investor/discover_tab_screen"
}

@Serializable
data object HistoryTabScreen : NavBarScreen {
    override val title: String = "History"
}

@Serializable
data class TidbitDetailScreen(val tidbitId: String) : Screen

@Serializable
data object TidbitScreen : Screen

@Serializable
data object SavedTidbitScreen : Screen
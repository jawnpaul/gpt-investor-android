package com.thejawnpaul.gptinvestor.features.discover

import androidx.lifecycle.ViewModel
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class DiscoverTabsViewModel(@Provided private val analyticsLogger: AnalyticsLogger) : ViewModel() {

    fun onSectionSelected(section: DiscoverSection, isGuest: Boolean) {
        analyticsLogger.logEvent(
            eventName = "discover-section-selected",
            params = mapOf(
                "section" to section.name.lowercase(),
                "user_type" to if (isGuest) "guest" else "authenticated"
            )
        )
    }
}

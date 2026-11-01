package com.thejawnpaul.gptinvestor.features.notification.analytics

const val EVENT_NOTIFICATION_OPENED = "notification-opened"

private val KNOWN_SCREENS = listOf(
    "company_detail_screen",
    "tidbit_detail_screen",
    "digest_detail_screen",
    "discover"
)

fun notificationOpenedParams(deepLink: String?): Map<String, Any> = buildMap {
    if (deepLink != null) {
        put("deep_link", deepLink)
        put("target_screen", KNOWN_SCREENS.firstOrNull { deepLink.contains(it) } ?: "other")
        if (deepLink.contains("company_detail_screen/")) {
            deepLink.substringAfter("company_detail_screen/")
                .substringBefore("/")
                .takeIf { it.isNotBlank() }
                ?.let { put("ticker", it) }
        }
    }
}

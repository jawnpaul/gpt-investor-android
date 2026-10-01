package com.thejawnpaul.gptinvestor.core.platform

expect object Platform {
    val type: PlatformType
}

enum class PlatformType {
    Android,
    IOS
}

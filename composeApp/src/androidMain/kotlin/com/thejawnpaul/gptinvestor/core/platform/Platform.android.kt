package com.thejawnpaul.gptinvestor.core.platform

actual object Platform {
    actual val type: PlatformType
        get() = PlatformType.Android
}

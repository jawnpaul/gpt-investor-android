package com.thejawnpaul.gptinvestor.core.platform

interface PlatformCapabilities {
    val supportsGoogleSignIn: Boolean
    val supportsBilling: Boolean
    val supportsMediaPlayback: Boolean
    val supportsNotifications: Boolean
}

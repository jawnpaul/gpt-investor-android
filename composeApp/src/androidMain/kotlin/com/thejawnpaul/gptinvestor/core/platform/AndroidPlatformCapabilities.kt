package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.annotation.Factory

@Factory(binds = [PlatformCapabilities::class])
class AndroidPlatformCapabilities : PlatformCapabilities {
    override val supportsGoogleSignIn: Boolean = true
    override val supportsBilling: Boolean = true
    override val supportsMediaPlayback: Boolean = true
    override val supportsNotifications: Boolean = true
}

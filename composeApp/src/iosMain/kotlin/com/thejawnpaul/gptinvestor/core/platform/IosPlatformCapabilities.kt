package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.annotation.Factory

@Factory(binds = [PlatformCapabilities::class])
class IosPlatformCapabilities : PlatformCapabilities {
    override val supportsGoogleSignIn: Boolean = true
    override val supportsBilling: Boolean = false
    override val supportsMediaPlayback: Boolean = true
    override val supportsNotifications: Boolean = true
}

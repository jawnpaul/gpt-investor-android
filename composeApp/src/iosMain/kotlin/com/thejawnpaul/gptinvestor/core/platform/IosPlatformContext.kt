package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.annotation.Singleton

@Singleton(binds = [PlatformContext::class])
object IosPlatformContext : PlatformContext

package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.annotation.Singleton

@Singleton(binds = [PlatformContext::class])
class IosPlatformContext : PlatformContext

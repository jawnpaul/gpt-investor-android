package com.thejawnpaul.gptinvestor.core.platform

import android.content.Context
import org.koin.core.annotation.Factory

@Factory(binds = [PlatformContext::class])
class AndroidPlatformContext(val context: Context) : PlatformContext

package com.thejawnpaul.gptinvestor.remote

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
internal actual val isDebug: Boolean
    get() = Platform.isDebugBinary
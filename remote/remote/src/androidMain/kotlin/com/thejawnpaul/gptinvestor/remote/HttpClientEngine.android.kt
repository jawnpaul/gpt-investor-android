package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

internal actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = Android
package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = Darwin
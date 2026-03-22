package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.engine.HttpClientEngineFactory

internal expect fun getHttpClientEngine(): HttpClientEngineFactory<*>
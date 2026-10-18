package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier

internal actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = Darwin

internal actual fun getCurrentLocale(): String =
    NSLocale.currentLocale.localeIdentifier.replace('_', '-')
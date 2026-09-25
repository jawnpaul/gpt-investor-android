package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
import java.util.Locale

internal actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = Android

internal actual fun getCurrentLocale(): String = Locale.getDefault().toLanguageTag()
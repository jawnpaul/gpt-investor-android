package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.api.GeminiApi
import org.koin.core.module.Module

expect fun providesGeminiApi(api: GeminiApi?): Module
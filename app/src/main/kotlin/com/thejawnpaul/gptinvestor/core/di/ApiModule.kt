package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.api.createApiService
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.dsl.module

val apiServiceModule = module {

    single<ApiService> {
        get<Ktorfit>().createApiService()
    }
}
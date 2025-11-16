package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.api.createApiService
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.module.Module
import org.koin.dsl.module


expect val platformApiModule: Module
val apiServiceModule: List<Module> = module {

    single<ApiService> {
        get<Ktorfit>().createApiService()
    }
} + platformApiModule
package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        client
    }
}
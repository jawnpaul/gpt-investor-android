package com.thejawnpaul.gptinvestor.features.authentication.di

import cocoapods.FirebaseAuth.FIRAuth
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module

@OptIn(ExperimentalForeignApi::class)
actual val authModule = module {
    single<FIRAuth> { FIRAuth.auth() }
}
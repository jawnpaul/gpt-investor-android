package com.thejawnpaul.gptinvestor.core.preferences

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferenceModule = module {
    single<GPTInvestorPreferences> { GPTInvestorPreferences(androidContext()) }
}
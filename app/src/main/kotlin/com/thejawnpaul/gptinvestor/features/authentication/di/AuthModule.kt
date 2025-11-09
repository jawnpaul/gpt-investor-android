package com.thejawnpaul.gptinvestor.features.authentication.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

val authModule = module {
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
}

package com.thejawnpaul.gptinvestor.features.authentication.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
class AuthModule {

    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}

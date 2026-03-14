package com.thejawnpaul.gptinvestor.core.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
class AuthModule {

    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
}

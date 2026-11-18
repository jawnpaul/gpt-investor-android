package com.thejawnpaul.gptinvestor.core.platform

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.installations.installations
import org.koin.core.annotation.Singleton

interface InstallationIdProvider {
    suspend fun get(): String
}

@Singleton(binds = [InstallationIdProvider::class])
class FirebaseInstallationIdProvider : InstallationIdProvider {
    override suspend fun get(): String = Firebase.installations.getId()
}

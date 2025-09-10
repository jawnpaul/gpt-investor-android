@file:OptIn(ExperimentalForeignApi::class)

package com.thejawnpaul.gptinvestor.core.firebase

import cocoapods.FirebaseAuth.FIRUser
import kotlinx.cinterop.ExperimentalForeignApi
actual class User internal constructor(private val ios: FIRUser) {
    actual val uid: String
        get() = ios.uid()
    actual val email: String?
        get() = ios.email()
    actual val displayName: String?
        get() = ios.displayName()
    actual val providerId: String
        get() = ios.providerID()

    actual suspend fun delete(): Unit = ios.deleteWithCompletion {}
}

fun FIRUser.toUser(): User = User(this)
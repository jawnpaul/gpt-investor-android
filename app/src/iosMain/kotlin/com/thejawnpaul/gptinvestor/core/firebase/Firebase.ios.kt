@file:OptIn(ExperimentalForeignApi::class)

package com.thejawnpaul.gptinvestor.core.firebase

import cocoapods.FirebaseCore.FIRApp
import cocoapods.FirebaseCore.FIROptions
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred


public val FirebaseApp.ios: FIRApp
    get() = ios

public actual val Firebase.app: FirebaseApp
    get() = FirebaseApp(FIRApp.defaultApp()!!)

public actual fun Firebase.app(name: String): FirebaseApp =
    FirebaseApp(FIRApp.appNamed(name)!!)

public actual fun Firebase.initialize(context: Any?): FirebaseApp? = FIRApp.configure().let { app }

public actual fun Firebase.initialize(context: Any?, options: FirebaseOptions): FirebaseApp =
    FIRApp.configureWithOptions(options.toIos()).let { app }

public actual fun Firebase.initialize(context: Any?, options: FirebaseOptions, name: String): FirebaseApp =
    FIRApp.configureWithName(name, options.toIos()).let { app(name) }

public actual fun Firebase.apps(context: Any?): List<FirebaseApp> = FIRApp.allApps()
        .orEmpty()
        .values
        .map { FirebaseApp(it as FIRApp) }
public actual data class FirebaseApp internal constructor(internal val ios: FIRApp) {
    actual val name: String
        get() = ios.name
    actual val options: FirebaseOptions
        get() = ios.options.run {
            FirebaseOptions(
                applicationId = bundleID(),
                apiKey = APIKey()!!,
                databaseUrl = databaseURL()!!,
                storageBucket = storageBucket(),
                projectId = projectID(),
                gcmSenderId = GCMSenderID(),
            )
        }

    actual suspend fun delete() {
        val deleted = CompletableDeferred<Unit>()
        ios.deleteApp { deleted.complete(Unit) }
        deleted.await()
    }
}

private fun FirebaseOptions.toIos() = FIROptions(
    this.applicationId,
    this.gcmSenderId ?: ""
).apply {
    APIKey = apiKey
    databaseURL = databaseUrl
    storageBucket = this@toIos.storageBucket
    projectID = projectId
}

public actual open class FirebaseException(message: String) : Exception(message)
public actual open class FirebaseNetworkException(message: String) : FirebaseException(message)
public actual open class FirebaseTooManyRequestsException(message: String) : FirebaseException(message)
public actual open class FirebaseApiNotAvailableException(message: String) : FirebaseException(message)
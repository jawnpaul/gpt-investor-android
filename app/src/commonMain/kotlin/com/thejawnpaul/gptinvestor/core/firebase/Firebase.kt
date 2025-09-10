package com.thejawnpaul.gptinvestor.core.firebase

public object Firebase

public expect class FirebaseApp {
    public val name: String
    public val options: FirebaseOptions
    public suspend fun delete()
}

public expect val Firebase.app: FirebaseApp
public expect fun Firebase.app(name: String): FirebaseApp
public expect fun Firebase.apps(context: Any? = null): List<FirebaseApp>
public expect fun Firebase.initialize(context: Any? = null): FirebaseApp?
public expect fun Firebase.initialize(context: Any? = null, options: FirebaseOptions): FirebaseApp
public expect fun Firebase.initialize(context: Any? = null, options: FirebaseOptions, name: String): FirebaseApp

public val Firebase.options: FirebaseOptions
    get() = app.options

public data class FirebaseOptions(
    val applicationId: String,
    val apiKey: String,
    val databaseUrl: String? = null,
    val gaTrackingId: String? = null,
    val storageBucket: String? = null,
    val projectId: String? = null,
    val gcmSenderId: String? = null,
    val authDomain: String? = null,
)

public expect open class FirebaseException : Exception

public expect class FirebaseNetworkException : FirebaseException

public expect open class FirebaseTooManyRequestsException : FirebaseException

public expect open class FirebaseApiNotAvailableException : FirebaseException
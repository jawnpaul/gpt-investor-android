package com.thejawnpaul.gptinvestor.core.firebase

//import cocoapods.FirebaseAuth.FIRAuth
//import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
//import cocoapods.FirebaseCore.FIRApp
//import cocoapods.GoogleSignIn.GIDConfiguration
//import cocoapods.GoogleSignIn.GIDSignIn
//import kotlinx.cinterop.ExperimentalForeignApi
//import kotlinx.coroutines.channels.awaitClose
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//import platform.Foundation.NSError
//import platform.UIKit.UIApplication
//import platform.UIKit.UIWindow
//import platform.UIKit.UIWindowScene

/*@OptIn(ExperimentalForeignApi::class)
actual class FirebaseAuthentication(private val auth: FIRAuth) {
    actual suspend fun signOut() {
        auth.signOut(null)
    }

    actual fun getAuthenticationState(): Flow<Boolean> = callbackFlow {
        val handler = auth.addAuthStateDidChangeListener { _, user ->
            trySend(auth.currentUser() != null)
        }
        awaitClose {
            auth.removeAuthStateDidChangeListener(handler)
        }
    }

    actual val currentUser: User?
        get() = auth.currentUser()?.toUser()

    actual suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User?> {
        var result: Result<User?> = Result.failure(Exception("User not found"))
        auth.createUserWithEmail(email = email, password = password) { authResult, error ->
            val authData = authResult?.user()
            result = when {
                error != null -> Result.failure(error.toException())
                authData != null -> Result.success(authData.toUser())
                else -> Result.failure(Exception("Unknown error"))
            }
        }
        return result
    }

    actual suspend fun signInWithCredentials(): Result<User?> {
        var result: Result<User?> = Result.failure(Exception("User not found"))
        val clientID = FIRApp.defaultApp()?.options?.clientID
            ?: return Result.failure(Exception("Client ID not found"))
        val config = GIDConfiguration(clientID = clientID)
        GIDSignIn.sharedInstance.configuration = config
        val viewController = ((UIApplication.sharedApplication.connectedScenes.first()
                as? UIWindowScene)?.windows()?.first() as? UIWindow)?.rootViewController
            ?: return Result.failure(Exception("No root view controller found"))
        GIDSignIn
            .sharedInstance
            .signInWithPresentingViewController(
                presentingViewController = viewController
            ) { authResult, error ->
                {
                    result = when {
                        error != null -> Result.failure(error.toException())
                        authResult != null -> {
                            val idToken = authResult.user.idToken()?.tokenString()
                            val accessToken = authResult.user.accessToken.tokenString()
                            if (idToken != null) {
                                val credential = FIRGoogleAuthProvider.credentialWithIDToken(
                                    idToken = idToken,
                                    accessToken = accessToken
                                )
                                auth.signInWithCredential(credential) { signInResult, signInError ->
                                    result = when {
                                        signInError != null -> {
                                            Result.failure(signInError.toException())
                                        }
                                        signInResult?.user() != null -> {
                                            Result.success(signInResult.user().toUser())
                                        }

                                        else -> {
                                            Result.failure(
                                                Exception("Unknown error during sign-in")
                                            )
                                        }
                                    }
                                }
                                result
                            } else {
                                Result.failure(Exception("Failed to retrieve tokens"))
                            }
                        }

                        else -> Result.failure(Exception("Unknown error"))
                    }

                }
            }
        return result
    }

    actual suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User?> {
        var result: Result<User?> = Result.failure(Exception("User not found"))
        auth.signInWithEmail(email = email, password = password) { authResult, error ->
            val authData = authResult?.user()
            result = when {
                error != null -> Result.failure(error.toException())
                authData != null -> Result.success(authData.toUser())
                else -> Result.failure(Exception("Unknown error"))
            }
        }
        return result
    }
}

private fun NSError?.toException(): Exception {
    return this?.let { Exception(localizedDescription) } ?: Exception("Unknown error")
}*/

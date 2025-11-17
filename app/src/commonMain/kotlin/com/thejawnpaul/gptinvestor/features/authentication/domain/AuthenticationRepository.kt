package com.thejawnpaul.gptinvestor.features.authentication.domain

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import co.touchlab.kermit.Logger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.thejawnpaul.gptinvestor.BuildConfig
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.firebase.auth.User
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

interface AuthenticationRepository {
    val currentUser: User?
    suspend fun signUp(activityContext: Context): Flow<Boolean>
    suspend fun signOut()
    fun getAuthState(): Flow<Boolean>
    suspend fun deleteAccount()
    suspend fun loginWithEmailAndPassword(email: String, password: String): Flow<Boolean>
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Flow<Boolean>
    suspend fun loginWithGoogle(activityContext: Context): Flow<Boolean>
}

class AuthenticationRepositoryImpl(
    private val analyticsLogger: AnalyticsLogger,
    private val gptInvestorPreferences: GPTInvestorPreferences,
    private val tokenSyncManager: TokenSyncManager
) : AuthenticationRepository {
    override val currentUser: User?
        get() = auth.currentUser

    private fun getCredentialManager(activityContext: Context): CredentialManager = CredentialManager.create(activityContext)

    override suspend fun signUp(activityContext: Context): Flow<Boolean> = callbackFlow {
        try {
            val credentialManager = getCredentialManager(activityContext)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()

            // Create the Credential Manager request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            val credential = result.credential
            // Check if credential is of type Google ID
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                // Create Google ID Token
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                // Sign in to Firebase with using the token
                val credential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    trySend(task.isSuccessful)
                    getAuthState()
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            gptInvestorPreferences.setUserId(currentUser?.uid.toString())
                            gptInvestorPreferences.setIsUserLoggedIn(true)
                            tokenSyncManager.syncToken()
                        }
                        analyticsLogger.identifyUser(
                            eventName = "Sign Up",
                            params = mapOf(
                                "user_id" to currentUser?.uid.toString(),
                                "email" to currentUser?.email.toString(),
                                "name" to currentUser?.displayName.toString(),
                                "sign_up_method" to currentUser?.providerId.toString()
                            )
                        )
                    }
                }
            } else {
                Logger.e("Credential is not of type Google ID!")
            }
        } catch (e: Exception) {
            when (e) {
                is GetCredentialException -> {
                    Logger.e("Get Credential Exception: ${e.stackTraceToString()}")
                }

                else -> {
                    Logger.e("Unknown Exception: ${e.stackTraceToString()}")
                }
            }
        }
        awaitClose()
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
            gptInvestorPreferences.clearUserId()
            gptInvestorPreferences.clearIsUserLoggedIn()
            gptInvestorPreferences.clearThemePreference()
            gptInvestorPreferences.clearIsUserOnModelWaitlist()
            analyticsLogger.resetUser(eventName = "Log Out")
            val clearRequest = ClearCredentialStateRequest()
            val credentialManager = activityContext.getSystemService(CredentialManager::class.java)
            credentialManager.clearCredentialState(clearRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun deleteAccount() {
        try {
            currentUser?.delete()
            analyticsLogger.resetUser(eventName = "Delete Account")
            gptInvestorPreferences.clearUserId()
            gptInvestorPreferences.clearIsUserLoggedIn()
            gptInvestorPreferences.clearThemePreference()
            gptInvestorPreferences.clearIsFirstInstall()
            gptInvestorPreferences.clearIsUserOnModelWaitlist()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Flow<Boolean> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                trySend(task.isSuccessful)
                getAuthState()
                if (task.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        gptInvestorPreferences.setUserId(currentUser?.uid.toString())
                        gptInvestorPreferences.setIsUserLoggedIn(true)
                        tokenSyncManager.syncToken()
                    }
                    analyticsLogger.identifyUser(
                        eventName = "Log in",
                        params = mapOf(
                            "user_id" to currentUser?.uid.toString(),
                            "email" to email
                        )
                    )
                }
            }
            .addOnFailureListener { e ->
                Logger.e(e.stackTraceToString())
            }
        awaitClose()
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Flow<Boolean> = callbackFlow {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            trySend(task.isSuccessful)
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    tokenSyncManager.syncToken()
                }
                analyticsLogger.identifyUser(
                    eventName = "Sign Up",
                    params = mapOf(
                        "user_id" to currentUser?.uid.toString(),
                        "email" to email,
                        "name" to currentUser?.displayName.toString(),
                        "sign_up_method" to currentUser?.providerId.toString()
                    )
                )
            }
        }.addOnFailureListener {
            Logger.e(it.stackTraceToString())
        }
        awaitClose()
    }

    override suspend fun loginWithGoogle(activityContext: Context): Flow<Boolean> = callbackFlow {
        try {
            val credentialManager = getCredentialManager(activityContext)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()

            // Create the Credential Manager request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            val credential = result.credential
            // Check if credential is of type Google ID
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                // Create Google ID Token
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                // Sign in to Firebase with using the token
                val credential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    trySend(task.isSuccessful)
                    getAuthState()
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            tokenSyncManager.syncToken()
                        }
                        analyticsLogger.identifyUser(
                            eventName = "Sign Up",
                            params = mapOf(
                                "user_id" to currentUser?.uid.toString(),
                                "email" to currentUser?.email.toString(),
                                "name" to currentUser?.displayName.toString(),
                                "sign_up_method" to currentUser?.providerId.toString()
                            )
                        )
                    }
                }
            } else {
                Logger.e("Credential is not of type Google ID!")
            }
        } catch (e: Exception) {
            when (e) {
                is GetCredentialException -> {
                    Logger.e("Get Credential Exception: ${e.stackTraceToString()}")
                }

                else -> {
                    Logger.e("Unknown Exception: ${e.stackTraceToString()}")
                }
            }
        }

        awaitClose()
    }
}

package com.thejawnpaul.gptinvestor.features.authentication.domain

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.thejawnpaul.gptinvestor.BuildConfig
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.features.conversation.data.firestore.ConversationSyncManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber

interface AuthenticationRepository {
    val currentUser: FirebaseUser?
    suspend fun signUp(activityContext: Context): Flow<Boolean>
    suspend fun signOut(activityContext: Context)
    fun getAuthState(): Flow<Boolean>
    suspend fun deleteAccount()
    suspend fun loginWithEmailAndPassword(email: String, password: String): Flow<Boolean>
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Flow<Boolean>
    suspend fun loginWithGoogle(activityContext: Context): Flow<Boolean>
}

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val analyticsLogger: AnalyticsLogger,
    private val gptInvestorPreferences: GPTInvestorPreferences,
    private val conversationSyncManager: ConversationSyncManager
) :
    AuthenticationRepository {
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    private fun getCredentialManager(activityContext: Context): CredentialManager {
        return CredentialManager.create(activityContext)
    }

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
                            gptInvestorPreferences.setUserId(auth.currentUser?.uid.toString())
                            gptInvestorPreferences.setIsUserLoggedIn(true)
                            conversationSyncManager.syncFromCloud().onSuccess {
                                Timber.d("Data sync successful after Google sign up")
                            }.onFailure { e ->
                                Timber.e(e, "Data sync failed after Google sign up")
                            }
                        }
                        analyticsLogger.identifyUser(
                            eventName = "Sign Up",
                            params = mapOf(
                                "user_id" to auth.currentUser?.uid.toString(),
                                "email" to auth.currentUser?.email.toString(),
                                "name" to auth.currentUser?.displayName.toString(),
                                "sign_up_method" to auth.currentUser?.providerId.toString()
                            )
                        )
                    }
                }
            } else {
                Timber.e("Credential is not of type Google ID!")
            }
        } catch (e: Exception) {
            when (e) {
                is GetCredentialException -> {
                    Timber.e("Get Credential Exception: ${e.stackTraceToString()}")
                }

                else -> {
                    Timber.e("Unknown Exception: ${e.stackTraceToString()}")
                }
            }
        }
        awaitClose()
    }

    override suspend fun signOut(activityContext: Context) {
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
            trySend(auth.currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun deleteAccount() {
        try {
            auth.currentUser?.delete()
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
                        gptInvestorPreferences.setUserId(auth.currentUser?.uid.toString())
                        gptInvestorPreferences.setIsUserLoggedIn(true)
                        conversationSyncManager.syncFromCloud().onSuccess {
                            Timber.d("Data sync successful after email/password login")
                        }.onFailure { e ->
                            Timber.e(e, "Data sync failed after email/password login")
                        }
                    }
                    analyticsLogger.identifyUser(
                        eventName = "Log in",
                        params = mapOf(
                            "user_id" to auth.currentUser?.uid.toString(),
                            "email" to email
                        )
                    )
                }
            }
            .addOnFailureListener { e ->
                Timber.e(e.stackTraceToString())
            }
        awaitClose()
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Flow<Boolean> = callbackFlow {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            trySend(task.isSuccessful)
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    gptInvestorPreferences.setUserId(auth.currentUser?.uid.toString())
                    gptInvestorPreferences.setIsUserLoggedIn(true)
                    conversationSyncManager.syncFromCloud().onSuccess {
                        Timber.d("Data sync successful after email/password sign up")
                    }.onFailure { e ->
                        Timber.e(e, "Data sync failed after email/password sign up")
                    }
                }
                analyticsLogger.identifyUser(
                    eventName = "Sign Up",
                    params = mapOf(
                        "user_id" to auth.currentUser?.uid.toString(),
                        "email" to email,
                        "name" to auth.currentUser?.displayName.toString(),
                        "sign_up_method" to auth.currentUser?.providerId.toString()
                    )
                )
            }
        }.addOnFailureListener {
            Timber.e(it.stackTraceToString())
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
                            gptInvestorPreferences.setUserId(auth.currentUser?.uid.toString())
                            gptInvestorPreferences.setIsUserLoggedIn(true)
                            conversationSyncManager.syncFromCloud().onSuccess {
                                Timber.d("Data sync successful after Google login")
                            }.onFailure { e ->
                                Timber.e(e, "Data sync failed after Google login")
                            }
                        }
                        analyticsLogger.identifyUser(
                            eventName = "Sign Up", // Should this be "Log in" for loginWithGoogle?
                            params = mapOf(
                                "user_id" to auth.currentUser?.uid.toString(),
                                "email" to auth.currentUser?.email.toString(),
                                "name" to auth.currentUser?.displayName.toString(),
                                "sign_up_method" to auth.currentUser?.providerId.toString()
                            )
                        )
                    }
                }
            } else {
                Timber.e("Credential is not of type Google ID!")
            }
        } catch (e: Exception) {
            when (e) {
                is GetCredentialException -> {
                    Timber.e("Get Credential Exception: ${e.stackTraceToString()}")
                }

                else -> {
                    Timber.e("Unknown Exception: ${e.stackTraceToString()}")
                }
            }
        }

        awaitClose()
    }
}

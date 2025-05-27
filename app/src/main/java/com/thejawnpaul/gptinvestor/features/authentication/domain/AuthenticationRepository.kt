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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

interface AuthenticationRepository {
    val currentUser: FirebaseUser?
    suspend fun signUp(): Flow<Boolean>
    suspend fun signOut()
    fun getAuthState(): Flow<Boolean>
    suspend fun deleteAccount()
    suspend fun loginWithEmailAndPassword(email: String, password: String): Flow<Boolean>
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Flow<Boolean>
    suspend fun loginWithGoogle(): Flow<Boolean>
}

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context,
    private val analyticsLogger: AnalyticsLogger
) :
    AuthenticationRepository {
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    override suspend fun signUp(): Flow<Boolean> = callbackFlow {
        try {
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
                context = context
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

    override suspend fun signOut() {
        try {
            auth.signOut()
            val clearRequest = ClearCredentialStateRequest()
            val credentialManager = context.getSystemService(CredentialManager::class.java)
            credentialManager.clearCredentialState(clearRequest)
            analyticsLogger.resetUser(eventName = "Log Out")
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

    override suspend fun loginWithGoogle(): Flow<Boolean> = callbackFlow {
        try {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(true)
                    .build()

                // Create the Credential Manager request
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
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
        } catch (e: Exception) {
        }
        awaitClose()
    }
}

package com.thejawnpaul.gptinvestor.features.authentication.domain

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

interface AuthenticationRepository {
    val currentUser: FirebaseUser?
    suspend fun signUp(launcher: ActivityResultLauncher<Intent>)
    suspend fun signOut()
    fun getAuthState(): Flow<Boolean>
    suspend fun deleteAccount()
    suspend fun login(email: String, password: String): Flow<Boolean>
}

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context,
    private val analyticsLogger: AnalyticsLogger
) :
    AuthenticationRepository {
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signUp(launcher: ActivityResultLauncher<Intent>) {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        launcher.launch(signInIntent)
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
            AuthUI.getInstance().signOut(context)
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
            AuthUI.getInstance().delete(context)
            analyticsLogger.resetUser(eventName = "Delete Account")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun login(email: String, password: String): Flow<Boolean> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                trySend(task.isSuccessful)
                getAuthState()
                if (task.isSuccessful) {
                    analyticsLogger.identifyUser(
                        eventName = "Log in",
                        params = mapOf("user_id" to auth.currentUser?.uid.toString(), "email" to email)
                    )
                }
            }
            .addOnFailureListener { e ->
                Timber.e(e.stackTraceToString())
            }
        awaitClose()
    }
}

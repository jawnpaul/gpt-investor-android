package com.thejawnpaul.gptinvestor.features.authentication.domain

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface AuthenticationRepository {
    val currentUser: FirebaseUser?
    suspend fun signIn(launcher: ActivityResultLauncher<Intent>)
    suspend fun signOut()
    fun getAuthState(): Flow<Boolean>
    suspend fun deleteAccount()
}

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) :
    AuthenticationRepository {
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signIn(launcher: ActivityResultLauncher<Intent>) {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

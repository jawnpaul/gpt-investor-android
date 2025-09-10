package com.thejawnpaul.gptinvestor.core.firebase

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.thejawnpaul.gptinvestor.BuildConfig
import com.thejawnpaul.gptinvestor.core.utility.ActivityContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class FirebaseAuthentication(private val auth: FirebaseAuth) {
    actual suspend fun signOut() {
        val derivedContext = ActivityContext.get()
            ?: throw IllegalArgumentException("Context must be an instance of android.content.Context")
        auth.signOut()
        val clearRequest = ClearCredentialStateRequest()
        val credentialManager = derivedContext.getSystemService(CredentialManager::class.java)
        credentialManager.clearCredentialState(clearRequest)
    }

    actual fun getAuthenticationState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    actual val currentUser: User?
        get() = auth.currentUser?.toUser()

    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<User?> {
        var result: Result<User?> = Result.failure(Exception("User not found"))
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = Result.success(task.result?.user?.toUser())
                }
            }.addOnFailureListener {
                result = Result.failure(it)
            }
        return result
    }

    actual suspend fun signInWithCredentials(): Result<User?> {
        var result: Result<User?> = Result.failure(Exception("User not found"))
        val context = ActivityContext.get() ?: return Result.failure(Exception("Invalid context"))
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .build()

        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credResult = credentialManager.getCredential(
            request = request,
            context = context
        )
        val credential = credResult.credential
//         Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            val credential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = Result.success(task.result?.user?.toUser())
                }
            }.addOnFailureListener {
                result = Result.failure(it)
            }
        }
        return result
    }

    actual suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User?> {
        var result: Result<User?> = Result.failure(Exception("User not found"))
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = Result.success(task.result?.user?.toUser())
                }
            }.addOnFailureListener {
                result = Result.failure(it)
            }
        return result
    }
}
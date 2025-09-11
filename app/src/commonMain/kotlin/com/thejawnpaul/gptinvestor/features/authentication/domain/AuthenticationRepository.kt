package com.thejawnpaul.gptinvestor.features.authentication.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.firebase.IFirebaseAuth
import com.thejawnpaul.gptinvestor.core.firebase.IUser
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

interface AuthenticationRepository {
    val currentUser: IUser?
    suspend fun signUp(): Flow<Boolean>
    suspend fun signOut()
    fun getAuthState(): Flow<Boolean>
    suspend fun deleteAccount()
    suspend fun loginWithEmailAndPassword(email: String, password: String): Flow<Boolean>
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Flow<Boolean>
    suspend fun loginWithGoogle(): Flow<Boolean>
}

@Single
class AuthenticationRepositoryImpl(
    private val auth: IFirebaseAuth,
    private val analyticsLogger: AnalyticsLogger,
    private val gptInvestorPreferences: GPTInvestorPreferences
) :
    AuthenticationRepository {
    override val currentUser: IUser?
        get() = auth.currentUser

    override suspend fun signUp(): Flow<Boolean> = callbackFlow {
        try {
            auth.signInWithCredentials().onSuccess { task ->
                trySend(task != null)
                getAuthState()
                if (task != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        gptInvestorPreferences.setUserId(auth.currentUser?.uid.toString())
                        gptInvestorPreferences.setIsUserLoggedIn(true)
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

        } catch (e: Exception) {
            Logger.e("Unknown Exception: ${e.stackTraceToString()}", e)
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAuthState(): Flow<Boolean> = auth.getAuthenticationState()

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

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Flow<Boolean> =
        callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .onSuccess { task ->
                trySend(task != null)
                getAuthState()
                if (task != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        gptInvestorPreferences.setUserId(auth.currentUser?.uid.toString())
                        gptInvestorPreferences.setIsUserLoggedIn(true)
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
            .onFailure { e ->
                Logger.e(e.stackTraceToString())
            }
            awaitClose()
        }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Boolean> =
        callbackFlow {
        auth.createUserWithEmailAndPassword(email, password).onSuccess { task ->
            trySend(task != null)
            if (task != null) {
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
        }.onFailure {
            Logger.e(it.stackTraceToString())
        }
            awaitClose()
        }

    override suspend fun loginWithGoogle(): Flow<Boolean> = callbackFlow {
        try {
            auth.signInWithCredentials().onSuccess { task ->
                trySend(task != null)
                getAuthState()
                if (task != null) {
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
        } catch (e: Exception) {
            Logger.e("Unknown Exception: ${e.stackTraceToString()}")
        }

        awaitClose()
    }
}

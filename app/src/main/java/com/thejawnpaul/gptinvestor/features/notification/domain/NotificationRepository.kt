package com.thejawnpaul.gptinvestor.features.notification.domain

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.features.notification.data.RegisterTokenRequest
import javax.inject.Inject
import timber.log.Timber

interface NotificationRepository {

    suspend fun registerToken(token: String)

    suspend fun generateToken()
}

class NotificationRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : NotificationRepository {
    override suspend fun registerToken(token: String) {
        try {
            apiService.registerToken(RegisterTokenRequest(token))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
        }
    }

    override suspend fun generateToken() {
        try {
            Firebase.messaging.token.addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Timber.e(task.exception)
                        return@OnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result

                    Timber.e("Token: $token")
                }
            )
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
        }
    }
}

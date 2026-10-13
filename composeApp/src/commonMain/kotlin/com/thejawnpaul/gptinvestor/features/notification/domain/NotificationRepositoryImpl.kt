package com.thejawnpaul.gptinvestor.features.notification.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.notification.data.RegisterTokenRequest
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Singleton

private const val MAX_REGISTER_ATTEMPTS = 3
private const val INITIAL_RETRY_DELAY_MS = 2_000L

@Singleton(binds = [NotificationRepository::class])
class NotificationRepositoryImpl(private val apiService: KtorApiService, private val preferences: AppPreferences) :
    NotificationRepository {

    override suspend fun saveToken(token: String) {
        if (token == preferences.fcmToken.first()) return
        preferences.setFcmToken(token)
        preferences.setIsTokenSynced(false)
        Logger.e { "FCM token saved to preferences." }
    }

    override suspend fun syncTokenIfNeeded() {
        val isGuest = preferences.isGuestLoggedIn.first() == true
        if (isGuest) {
            Logger.e { "Token sync skipped: guest session." }
            return
        }

        val userId = preferences.userId.first()
        if (userId == null) {
            Logger.e { "Token sync skipped: no user." }
            return
        }

        val token = fetchCurrentToken() ?: preferences.fcmToken.first()
        if (token == null) {
            Logger.e { "Token sync skipped: no token." }
            return
        }
        saveToken(token)

        if (preferences.isTokenSynced.first()) {
            Logger.e { "Token sync skipped: already registered." }
            return
        }

        Logger.e { "Attempting to sync token for user: $userId" }
        registerToken(token)
    }

    private suspend fun fetchCurrentToken(): String? = try {
        Firebase.messaging.getToken()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Logger.e(e) { "Failed to fetch FCM token" }
        null
    }

    private suspend fun registerToken(token: String) {
        var retryDelay = INITIAL_RETRY_DELAY_MS
        repeat(MAX_REGISTER_ATTEMPTS) { attempt ->
            try {
                val response = apiService.registerToken(RegisterTokenRequest(token = token))
                if (response.isSuccessful) {
                    preferences.setIsTokenSynced(true)
                    Logger.e { "FCM token successfully registered" }
                    return
                }
                Logger.e { "Failed to register FCM token (attempt ${attempt + 1}): HTTP ${response.code}" }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Logger.e(e) { "Failed to register FCM token (attempt ${attempt + 1})" }
            }
            if (attempt < MAX_REGISTER_ATTEMPTS - 1) {
                delay(retryDelay)
                retryDelay *= 2
            }
        }
    }
}

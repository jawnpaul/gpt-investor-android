package com.thejawnpaul.gptinvestor.core.preferences

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val themePreference: Flow<String?>
    suspend fun setThemePreference(theme: String)
    suspend fun clearThemePreference()

    val queryLastDate: Flow<String?>
    suspend fun setQueryLastDate(date: String)
    suspend fun clearQueryLastDate()

    val queryUsageCount: Flow<Int?>
    suspend fun setQueryUsageCount(count: Int)

    val notificationPermission: Flow<Boolean?>
    suspend fun setNotificationPermission(permission: Boolean)
    suspend fun clearNotificationPermission()

    val userId: Flow<String?>
    suspend fun setUserId(id: String)
    suspend fun clearUserId()

    val isFirstInstall: Flow<Boolean?>
    suspend fun setIsFirstInstall(isFirstInstall: Boolean)
    suspend fun clearIsFirstInstall()

    val isUserLoggedIn: Flow<Boolean?>
    suspend fun setIsUserLoggedIn(isLoggedIn: Boolean)
    suspend fun clearIsUserLoggedIn()

    val isGuestLoggedIn: Flow<Boolean?>
    suspend fun setIsGuestLoggedIn(isLoggedIn: Boolean)
    suspend fun clearIsGuestLoggedIn()

    val isUserOnModelWaitlist: Flow<Boolean?>
    suspend fun setIsUserOnModelWaitlist(isOnWaitlist: Boolean)
    suspend fun clearIsUserOnModelWaitlist()

    val fcmToken: Flow<String?>
    suspend fun setFcmToken(token: String)

    val isTokenSynced: Flow<Boolean>
    suspend fun setIsTokenSynced(isSynced: Boolean)

    val accessToken: Flow<String?>
    suspend fun setAccessToken(token: String)
    suspend fun clearAccessToken()

    val refreshToken: Flow<String?>
    suspend fun setRefreshToken(token: String)
    suspend fun clearRefreshToken()

    val userName: Flow<String?>
    suspend fun setUserName(name: String)
    suspend fun clearUserName()
}

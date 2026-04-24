package com.thejawnpaul.gptinvestor.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton(binds = [AppPreferences::class])
class DataStoreAppPreferences(private val dataStore: DataStore<Preferences>) : AppPreferences {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_preference")
        private val QUERY_LAST_DATE_KEY = stringPreferencesKey("query_last_day_preference")
        private val QUERY_USAGE_COUNT_KEY = intPreferencesKey("query_usage_count_preference")
        private val NOTIFICATION_PERMISSION_KEY =
            booleanPreferencesKey("notification_permission_preference")
        private val USER_ID_KEY = stringPreferencesKey("user_id_preference")
        private val IS_FIRST_INSTALL_KEY = booleanPreferencesKey("is_first_install_preference")
        private val IS_USER_LOGGED_IN_KEY = booleanPreferencesKey("is_user_logged_in_preference")
        private val USER_NAME_KEY = stringPreferencesKey("user_name_preference")
        private val IS_USER_ON_MODEL_WAITLIST_KEY =
            booleanPreferencesKey("is_user_on_model_waitlist_preference")
        private val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token_preference")
        private val IS_TOKEN_SYNCED_KEY = booleanPreferencesKey("is_token_synced_preference")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token_preference")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token_preference")
        private val IS_GUEST_LOGGED_IN_KEY = booleanPreferencesKey("is_guest_logged_in_preference")
    }

    override val themePreference: Flow<String?> = dataStore.data.map { preferences ->
        preferences[THEME_KEY]
    }

    override suspend fun setThemePreference(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    override suspend fun clearThemePreference() {
        dataStore.edit { preferences ->
            preferences.remove(THEME_KEY)
        }
    }

    override val queryLastDate: Flow<String?> = dataStore.data.map { preferences ->
        preferences[QUERY_LAST_DATE_KEY]
    }

    override suspend fun setQueryLastDate(date: String) {
        dataStore.edit { preferences ->
            preferences[QUERY_LAST_DATE_KEY] = date
        }
    }

    override suspend fun clearQueryLastDate() {
        dataStore.edit { preferences ->
            preferences.remove(QUERY_LAST_DATE_KEY)
        }
    }

    override val queryUsageCount: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[QUERY_USAGE_COUNT_KEY]
    }

    override suspend fun setQueryUsageCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[QUERY_USAGE_COUNT_KEY] = count
        }
    }

    override val notificationPermission: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_PERMISSION_KEY]
    }

    override suspend fun setNotificationPermission(permission: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_PERMISSION_KEY] = permission
        }
    }

    override suspend fun clearNotificationPermission() {
        dataStore.edit { preferences ->
            preferences.remove(NOTIFICATION_PERMISSION_KEY)
        }
    }

    override val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    override suspend fun setUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
        }
    }

    override suspend fun clearUserId() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }

    override val isFirstInstall: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_FIRST_INSTALL_KEY]
    }

    override suspend fun setIsFirstInstall(isFirstInstall: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_INSTALL_KEY] = isFirstInstall
        }
    }

    override suspend fun clearIsFirstInstall() {
        dataStore.edit { preferences ->
            preferences.remove(IS_FIRST_INSTALL_KEY)
        }
    }

    override val isUserLoggedIn: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_USER_LOGGED_IN_KEY]
    }

    override suspend fun setIsUserLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_USER_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    override suspend fun clearIsUserLoggedIn() {
        dataStore.edit { preferences ->
            preferences.remove(IS_USER_LOGGED_IN_KEY)
        }
    }

    override val isGuestLoggedIn: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_GUEST_LOGGED_IN_KEY]
    }

    override suspend fun setIsGuestLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_GUEST_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    override suspend fun clearIsGuestLoggedIn() {
        dataStore.edit { preferences ->
            preferences.remove(IS_GUEST_LOGGED_IN_KEY)
        }
    }

    override val isUserOnModelWaitlist: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_USER_ON_MODEL_WAITLIST_KEY]
    }

    override suspend fun setIsUserOnModelWaitlist(isOnWaitlist: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_USER_ON_MODEL_WAITLIST_KEY] = isOnWaitlist
        }
    }

    override suspend fun clearIsUserOnModelWaitlist() {
        dataStore.edit { preferences ->
            preferences.remove(IS_USER_ON_MODEL_WAITLIST_KEY)
        }
    }

    override val fcmToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[FCM_TOKEN_KEY]
    }

    override suspend fun setFcmToken(token: String) {
        dataStore.edit { preferences ->
            preferences[FCM_TOKEN_KEY] = token
        }
    }

    override val isTokenSynced: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_TOKEN_SYNCED_KEY] ?: false
    }

    override suspend fun setIsTokenSynced(isSynced: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_TOKEN_SYNCED_KEY] = isSynced
        }
    }

    override val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    override suspend fun setAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    override suspend fun clearAccessToken() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
        }
    }

    override val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    override suspend fun setRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }

    override suspend fun clearRefreshToken() {
        dataStore.edit { preferences ->
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }

    override val userName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }

    override suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    override suspend fun clearUserName() {
        dataStore.edit { preferences ->
            preferences.remove(USER_NAME_KEY)
        }
    }
}

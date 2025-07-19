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

@Singleton
class GPTInvestorPreferences (private val dataStore: DataStore<Preferences>) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_preference")
        private val QUERY_LAST_DATE_KEY = stringPreferencesKey("query_last_day_preference")
        private val QUERY_USAGE_COUNT_KEY = intPreferencesKey("query_usage_count_preference")
        private val NOTIFICATION_PERMISSION_KEY =
            booleanPreferencesKey("notification_permission_preference")
        private val USER_ID_KEY = stringPreferencesKey("user_id_preference")
        private val IS_FIRST_INSTALL_KEY = booleanPreferencesKey("is_first_install_preference")
        private val IS_USER_LOGGED_IN_KEY = booleanPreferencesKey("is_user_logged_in_preference")
        private val IS_USER_ON_MODEL_WAITLIST_KEY =
            booleanPreferencesKey("is_user_on_model_waitlist_preference")
    }

    val themePreference: Flow<String?> = dataStore.data.map { preferences ->
        preferences[THEME_KEY]
    }

    suspend fun setThemePreference(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    suspend fun clearThemePreference() {
        dataStore.edit { preferences ->
            preferences.remove(THEME_KEY)
        }
    }

    val queryLastDate: Flow<String?> = dataStore.data.map { preferences ->
        preferences[QUERY_LAST_DATE_KEY]
    }

    suspend fun setQueryLastDate(date: String) {
        dataStore.edit { preferences ->
            preferences[QUERY_LAST_DATE_KEY] = date
        }
    }

    suspend fun clearQueryLastDate() {
        dataStore.edit { preferences ->
            preferences.remove(QUERY_LAST_DATE_KEY)
        }
    }

    val queryUsageCount: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[QUERY_USAGE_COUNT_KEY]
    }

    suspend fun setQueryUsageCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[QUERY_USAGE_COUNT_KEY] = count
        }
    }

    val notificationPermission: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_PERMISSION_KEY]
    }

    suspend fun setNotificationPermission(permission: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_PERMISSION_KEY] = permission
        }
    }

    suspend fun clearNotificationPermission() {
        dataStore.edit { preferences ->
            preferences.remove(NOTIFICATION_PERMISSION_KEY)
        }
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    suspend fun setUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
        }
    }

    suspend fun clearUserId() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }

    val isFirstInstall: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_FIRST_INSTALL_KEY]
    }

    suspend fun setIsFirstInstall(isFirstInstall: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_INSTALL_KEY] = isFirstInstall
        }
    }

    suspend fun clearIsFirstInstall() {
        dataStore.edit { preferences ->
            preferences.remove(IS_FIRST_INSTALL_KEY)
        }
    }

    val isUserLoggedIn: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_USER_LOGGED_IN_KEY]
    }

    suspend fun setIsUserLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_USER_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    suspend fun clearIsUserLoggedIn() {
        dataStore.edit { preferences ->
            preferences.remove(IS_USER_LOGGED_IN_KEY)
        }
    }

    val isUserOnModelWaitlist: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_USER_ON_MODEL_WAITLIST_KEY]
    }

    suspend fun setIsUserOnModelWaitlist(isOnWaitlist: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_USER_ON_MODEL_WAITLIST_KEY] = isOnWaitlist
        }
    }

    suspend fun clearIsUserOnModelWaitlist() {
        dataStore.edit { preferences ->
            preferences.remove(IS_USER_ON_MODEL_WAITLIST_KEY)
        }
    }
}

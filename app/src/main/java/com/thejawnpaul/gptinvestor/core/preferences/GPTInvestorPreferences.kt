package com.thejawnpaul.gptinvestor.core.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class GPTInvestorPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_preference")
        private val Context.dataStore by preferencesDataStore("gpt_investor_preferences")
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
}

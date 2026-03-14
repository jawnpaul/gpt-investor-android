package com.thejawnpaul.gptinvestor.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

actual fun createDataStore(context: Any?): DataStore<Preferences> {
    require(context is Context) { "Android DataStore requires a Context" }
    return PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("gpt_investor_preferences") }
    )
}

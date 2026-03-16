package com.thejawnpaul.gptinvestor.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(context: Any? = null): DataStore<Preferences>

internal const val DATA_STORE_FILE_NAME = "gpt_investor_preferences.preferences_pb"

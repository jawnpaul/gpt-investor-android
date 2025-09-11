package com.thejawnpaul.gptinvestor.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(context: Context): DataStore<Preferences> = createDataStore {
    context.filesDir.resolve(DataStoreFileName).absolutePath
}

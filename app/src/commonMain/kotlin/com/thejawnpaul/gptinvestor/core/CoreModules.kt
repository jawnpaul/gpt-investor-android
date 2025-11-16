package com.thejawnpaul.gptinvestor.core

import com.thejawnpaul.gptinvestor.core.di.apiServiceModule
import com.thejawnpaul.gptinvestor.core.di.bindingModule
import com.thejawnpaul.gptinvestor.core.di.coroutinesModule
import com.thejawnpaul.gptinvestor.core.di.coroutinesScopeModule
import com.thejawnpaul.gptinvestor.core.di.databaseModule
import com.thejawnpaul.gptinvestor.core.firebase.remoteconfig.remoteConfigModule
import com.thejawnpaul.gptinvestor.core.preferences.preferenceModule

val coreModules = listOf(
    bindingModule,
    coroutinesModule,
    coroutinesScopeModule,
    preferenceModule,
    remoteConfigModule
).plus(databaseModule).plus(apiServiceModule)
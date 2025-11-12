package com.thejawnpaul.gptinvestor.core

import com.thejawnpaul.gptinvestor.core.di.apiServiceModule
import com.thejawnpaul.gptinvestor.core.di.bindingModule
import com.thejawnpaul.gptinvestor.core.di.coroutinesModule
import com.thejawnpaul.gptinvestor.core.di.coroutinesScopeModule
import com.thejawnpaul.gptinvestor.core.di.databaseModule
import com.thejawnpaul.gptinvestor.core.preferences.preferenceModule
import com.thejawnpaul.gptinvestor.core.remoteconfig.remoteConfigModule

val coreModules = listOf(
    apiServiceModule,
    bindingModule,
    coroutinesModule,
    coroutinesScopeModule,
    databaseModule,
    preferenceModule,
    remoteConfigModule
)
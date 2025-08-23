package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.IRemoteConfig
import org.koin.core.module.Module

expect fun providesRemoteConfig(config: IRemoteConfig?): Module
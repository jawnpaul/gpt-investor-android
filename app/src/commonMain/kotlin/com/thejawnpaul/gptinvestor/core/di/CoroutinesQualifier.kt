package com.thejawnpaul.gptinvestor.core.di

import org.koin.core.qualifier.named

val IosDispatcher = named("IoDispatcher")

val MainDispatcher = named("MainDispatcher")

val DefaultDispatcher = named("DefaultDispatcher")

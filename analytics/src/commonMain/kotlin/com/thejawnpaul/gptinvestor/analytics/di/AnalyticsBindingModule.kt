package com.thejawnpaul.gptinvestor.analytics.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named


val mixpanelAnalytics = named("MixpanelAnalytics")
val firebaseAnalytics = named("FirebaseAnalytics")

expect val analyticsModule: Module
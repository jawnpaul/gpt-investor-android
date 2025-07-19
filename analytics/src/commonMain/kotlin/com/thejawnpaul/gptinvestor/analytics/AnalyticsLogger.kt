package com.thejawnpaul.gptinvestor.analytics

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("AnalyticsLogger", exact = true)
interface AnalyticsLogger {
    fun logEvent(eventName: String, params: Map<String, Any>)
    fun logViewEvent(screenName: String)
    fun identifyUser(eventName: String, params: Map<String, Any>)
    fun resetUser(eventName: String)
}
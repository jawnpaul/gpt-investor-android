package com.thejawnpaul.gptinvestor.core.remoteconfig

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton

@Singleton(binds = [RemoteConfigClient::class])
class KmpRemoteConfig : RemoteConfigClient {
    private val remoteConfig = Firebase.remoteConfig

    override fun init() {
        // Defaults are set via fallback values in fetch methods or can be set here if GitLive supports it
        // GitLive 2.4.0 supports setDefaults(defaults: Map<String, Any>)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                remoteConfig.setDefaults(
                    "prompt_count" to 100,
                    "default_model_name" to "gemini-2.0-flash",
                    "free_prompt_count" to 2,
                    "website_domain" to "https://m7mxrc35hp.us-west-2.awsapprunner.com/"
                )
                remoteConfig.fetchAndActivate()
            } catch (e: Exception) {
                // Ignore for now
            }
        }
    }

    override fun fetchAndActivateStringValue(configKey: String): String? = try {
        remoteConfig.getValue(configKey).asString()
    } catch (e: Exception) {
        null
    }

    override fun fetchAndActivateDoubleValue(configKey: String): Double = try {
        remoteConfig.getValue(configKey).asDouble()
    } catch (e: Exception) {
        0.0
    }

    override fun fetchAndActivateBooleanValue(configKey: String): Boolean = try {
        remoteConfig.getValue(configKey).asBoolean()
    } catch (e: Exception) {
        false
    }
}

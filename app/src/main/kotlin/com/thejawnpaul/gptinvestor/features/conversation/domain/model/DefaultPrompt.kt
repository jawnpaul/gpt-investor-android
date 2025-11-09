package com.thejawnpaul.gptinvestor.features.conversation.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Types

data class DefaultPrompt(
    val title: String,
    val query: String
)

@JsonClass(generateAdapter = true)
data class RemoteConfigPrompt(
    @field:Json(name = "_id") val id: RemoteConfigId? = null,
    @field:Json(name = "label")val label: String? = null,
    @field:Json(name = "query")val query: String? = null
)

@JsonClass(generateAdapter = true)
data class RemoteConfigId(
    @field:Json(name = "\$oid")val id: String? = null
)

class DefaultPromptParser {
    private val moshi = com.squareup.moshi.Moshi.Builder()
        .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter: com.squareup.moshi.JsonAdapter<List<RemoteConfigPrompt>> =
        moshi.adapter(
            Types.newParameterizedType(
                List::class.java,
                RemoteConfigPrompt::class.java
            )
        )

    fun parseDefaultPrompts(jsonString: String): List<RemoteConfigPrompt>? {
        return try {
            jsonAdapter.fromJson(jsonString)
        } catch (e: Exception) {
            println("Error parsing JSON: $jsonString")
            // println("Error parsing JSON: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}

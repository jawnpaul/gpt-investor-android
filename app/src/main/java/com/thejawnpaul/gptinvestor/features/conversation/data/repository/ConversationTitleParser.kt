package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import timber.log.Timber

@JsonClass(generateAdapter = true)
data class ConversationTitle(
    @field:Json(name = "title") val title: String
)

class ConversationTitleParser {

    private val moshi = Moshi.Builder()
        .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter: JsonAdapter<ConversationTitle> =
        moshi.adapter(ConversationTitle::class.java)

    fun parseTitle(jsonString: String): ConversationTitle? {
        return try {
            jsonAdapter.fromJson(jsonString)
        } catch (e: Exception) {
            Timber.e("Error parsing JSON: ${e.message}")
            null
        }
    }
}
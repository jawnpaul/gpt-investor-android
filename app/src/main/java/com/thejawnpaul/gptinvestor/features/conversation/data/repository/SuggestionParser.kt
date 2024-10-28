package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import timber.log.Timber

@JsonClass(generateAdapter = true)
data class SuggestionsResponse(
    @field:Json(name = "suggestions") val suggestions: List<Suggestion>
)

@JsonClass(generateAdapter = true)
data class Suggestion(
    @field:Json(name = "label") val label: String,
    @field:Json(name = "query") val query: String
)

class SuggestionParser {
    private val moshi = Moshi.Builder()
        .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter: JsonAdapter<SuggestionsResponse> =
        moshi.adapter(SuggestionsResponse::class.java)

    fun parseSuggestions(jsonString: String): SuggestionsResponse? {
        return try {
            jsonAdapter.fromJson(jsonString)
        } catch (e: Exception) {
            Timber.e("Error parsing JSON: ${e.message}")
            null
        }
    }
}

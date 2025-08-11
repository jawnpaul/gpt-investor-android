package com.thejawnpaul.gptinvestor.features.tidbit.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TidbitRemote(
    @field:Json(name = "_id") val id: String,
    @field:Json(name = "category") val category: String,
    @field:Json(name = "content") val content: String,
    @field:Json(name = "created_at") val createdAt: String,
    @field:Json(name = "created_by") val createdBy: String,
    @field:Json(name = "for_date") val forDate: String,
    @field:Json(name = "impressions") val impressions: Int? = 0,
    @field:Json(name = "media_url") val mediaUrl: String,
    @field:Json(name = "original_author") val originalAuthor: String,
    @field:Json(name = "preview_url") val previewUrl: String,
    @field:Json(name = "source") val source: String,
    @field:Json(name = "tidbit_type") val type: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "updated_at") val updatedAt: String
)

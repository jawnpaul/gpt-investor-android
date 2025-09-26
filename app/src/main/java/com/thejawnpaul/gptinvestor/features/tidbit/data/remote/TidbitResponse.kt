package com.thejawnpaul.gptinvestor.features.tidbit.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TidbitRemote(
    @SerialName("_id") val id: String,
    val category: String,
    val content: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("created_by") val createdBy: String,
    @SerialName("for_date") val forDate: String,
    val impressions: Int? = 0,
    @SerialName("media_url") val mediaUrl: String,
    @SerialName("original_author") val originalAuthor: String,
    @SerialName("preview_url") val previewUrl: String,
    val source: String,
    @SerialName("tidbit_type") val type: String,
    val title: String,
    @SerialName("is_liked") val isLiked: Boolean? = false,
    @SerialName("is_bookmarked") val isBookmarked: Boolean? = false,
    @SerialName("updated_at") val updatedAt: String,
    val summary: String = ""

)

@Serializable
data class AllTidbitResponse(
    val page: Int,
    @SerialName("page_size") val pageSize: Int,
    @SerialName("total_tidbits") val totalTidbit: Int,
    @SerialName("tidbits") val data: List<TidbitRemote>
)

@Serializable
data class TidbitLikeRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("tidbit_id") val tidbitId: String
)

@Serializable
data class TidbitLikeResponse(
    val message: String
)

@Serializable
data class TidbitBookmarkResponse(
    val message: String
)

@Serializable
data class TidbitBookmarkRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("tidbit_id") val tidbitId: String
)

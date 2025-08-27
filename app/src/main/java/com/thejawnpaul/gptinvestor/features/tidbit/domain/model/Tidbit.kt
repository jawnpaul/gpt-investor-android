package com.thejawnpaul.gptinvestor.features.tidbit.domain.model

data class Tidbit(
    val id: String,
    val previewUrl: String,
    val mediaUrl: String,
    val title: String,
    val content: String,
    val originalAuthor: String,
    val category: String,
    val sourceUrl: String,
    val type: String
) {
    val description = content.take(400)
}

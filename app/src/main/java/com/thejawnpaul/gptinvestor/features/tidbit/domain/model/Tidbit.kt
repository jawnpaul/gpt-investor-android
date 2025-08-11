package com.thejawnpaul.gptinvestor.features.tidbit.domain.model

data class Tidbit(
    val id: String,
    val previewUrl: String,
    val title: String,
    val content: String
) {
    val description = content.take(400)
}

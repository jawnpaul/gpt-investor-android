package com.thejawnpaul.gptinvestor.features.tidbit.presentation.state

data class HomeTidbitView(
    val loading: Boolean = false,
    val id: String = "",
    val previewUrl: String = "",
    val title: String = "",
    val description: String = "",
    val error: String? = null
)

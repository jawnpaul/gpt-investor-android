package com.thejawnpaul.gptinvestor.features.tidbit.presentation.model

sealed interface TidbitPresentation {
    val id: String
    val title: String
    val content: String
    val originalAuthor: String
    val category: String
    val previewUrl: String
    val isBookmarked: Boolean
    val isLiked: Boolean

    data class ArticlePresentation(
        override val id: String,
        override val title: String = "",
        override val content: String = "",
        override val originalAuthor: String = "",
        override val category: String = "",
        override val previewUrl: String = "",
        override val isBookmarked: Boolean = false,
        override val isLiked: Boolean = false,
        val name: String,
        val mediaUrl: String = "",
        val sourceUrl: String = ""
    ) : TidbitPresentation

    data class VideoPresentation(
        override val title: String = "",
        override val id: String,
        override val content: String = "",
        override val originalAuthor: String = "",
        override val category: String = "",
        override val previewUrl: String = "",
        override val isBookmarked: Boolean = false,
        override val isLiked: Boolean = false,
        val name: String,
        val mediaUrl: String = "",
        val sourceUrl: String = ""
    ) : TidbitPresentation {

        val videoId: String? = getYoutubeVideoId(mediaUrl)

        private fun getYoutubeVideoId(url: String?): String? {
            if (url == null) return null
            val patterns = listOf(
                "(?<=watch\\?v=)[\\w-]+".toRegex(),
                "(?<=embed/)[\\w-]+(?:\\?|$)".toRegex(),
                "(?<=youtu.be/)[\\w-]+".toRegex()
            )

            return patterns.firstNotNullOfOrNull { pattern ->
                pattern.find(url)?.value?.takeWhile { it != '?' }
            }
        }
    }

    data class AudioPresentation(
        override val id: String,
        override val title: String = "",
        override val content: String = "",
        override val originalAuthor: String = "",
        override val category: String = "",
        override val previewUrl: String = "",
        override val isBookmarked: Boolean = false,
        override val isLiked: Boolean = false,
        val name: String,
        val mediaUrl: String = "",
        val sourceUrl: String = ""
    ) : TidbitPresentation
}

package com.thejawnpaul.gptinvestor.features.news.domain

import androidx.paging.PagingData
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.news.domain.model.Explainer
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsFeedMeta
import com.thejawnpaul.gptinvestor.features.news.domain.model.Story
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getFeedPaged(filter: String, onFirstPage: (NewsFeedMeta) -> Unit): Flow<PagingData<Story>>

    suspend fun getStory(id: String): Either<Failure, Story>

    suspend fun getTopStory(): Either<Failure, Story?>

    suspend fun getExplainer(storyId: String): Either<Failure, Explainer>

    suspend fun getShareImage(url: String): Either<Failure, ByteArray>

    fun getShareUrl(storyId: String): String
}

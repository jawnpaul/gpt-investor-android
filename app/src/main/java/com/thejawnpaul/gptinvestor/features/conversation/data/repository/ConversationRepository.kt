package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class ConversationRepository @Inject constructor(private val apiService: ApiService) :
    IConversationRepository {
    override suspend fun getDefaultPrompts(): Flow<Either<Failure, List<DefaultPrompt>>> = flow {
        try {
            val response = apiService.getDefaultPrompts()
            if (response.isSuccessful) {
                response.body()?.let { prompts ->
                    val defaultPrompts = prompts.map { prompt ->
                        with(prompt) {
                            DefaultPrompt(title = label, query = query)
                        }
                    }.take(8)
                    emit(Either.Right(defaultPrompts))
                }
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }
}

package com.thejawnpaul.gptinvestor.features.onboarding.data.repository

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.platform.InstallationIdProvider
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.MarkQuestionAskedRequest
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingProgress
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestion
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSync
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.QuestionState
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingQuestionRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.annotation.Singleton

@Singleton(binds = [IOnboardingQuestionRepository::class])
class OnboardingQuestionRepositoryImpl(
    private val apiService: KtorApiService,
    private val preferences: AppPreferences,
    private val installationIdProvider: InstallationIdProvider
) : IOnboardingQuestionRepository {

    private val progress = MutableStateFlow<OnboardingProgress?>(null)
    private val syncLock = Mutex()

    override fun getQuestionState(question: OnboardingQuestion): Flow<QuestionState?> =
        combine(progress, ownerFlow()) { current, owner ->
            current?.takeIf { it.owner == owner }?.stateOf(question)
        }.distinctUntilChanged()

    override suspend fun getProgress(): Either<Failure, OnboardingProgress> = syncLock.withLock {
        progress.value?.takeIf { it.owner == owner() }?.let { return@withLock Either.Right(it) }
        when (val synced = fetch()) {
            is Either.Left -> synced
            is Either.Right -> Either.Right(synced.b.progress)
        }
    }

    override suspend fun sync(): Either<Failure, OnboardingSync> = syncLock.withLock { fetch() }

    private suspend fun fetch(): Either<Failure, OnboardingSync> {
        val owner = owner()
        val result = try {
            val response = apiService.syncOnboarding(installationId())
            val body = response.body
            if (response.isSuccessful && body != null) {
                Either.Right(
                    OnboardingSync(
                        progress = OnboardingProgress(
                            owner = owner,
                            answers = body.answers.mapNotNull { (id, answer) -> answer?.let { id to it } }.toMap(),
                            asked = body.asked.toSet()
                        ),
                        addedStock = body.addedStock
                    )
                )
            } else {
                Either.Left(Failure.ServerError)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Either.Left(Failure.NetworkConnection)
        }
        if (result is Either.Right) {
            progress.value = result.b.progress
            migrateLegacyScreen(result.b.progress)
        }
        return result
    }

    override suspend fun markAsked(question: OnboardingQuestion): Either<Failure, Unit> {
        syncLock.withLock { recordAsked(question) }
        return postAsked(question)
    }

    override suspend fun saveAnswer(question: OnboardingQuestion, answer: String): Either<Failure, Unit> {
        val isCached = syncLock.withLock {
            progress.update { it?.copy(answers = it.answers + (question.id to answer), asked = it.asked + question.id) }
            progress.value?.owner == owner()
        }
        val result =
            call { apiService.saveOnboardingAnswers(installationId(), buildJsonObject { put(question.id, answer) }) }
        if (result is Either.Right && !isCached) getProgress()
        return result
    }

    private fun recordAsked(question: OnboardingQuestion) {
        progress.update { it?.copy(asked = it.asked + question.id) }
    }

    private suspend fun postAsked(question: OnboardingQuestion): Either<Failure, Unit> =
        call { apiService.markOnboardingQuestionAsked(installationId(), MarkQuestionAskedRequest(question.id)) }

    private suspend fun migrateLegacyScreen(current: OnboardingProgress) {
        if (current.owner == GUEST_OWNER) return
        if (preferences.hasCompletedPostAuthOnboarding.first() != true) return
        val results = LEGACY_SCREEN_QUESTIONS.filterNot { current.stateOf(it).isSettled }.map { question ->
            recordAsked(question)
            postAsked(question)
        }
        if (results.all { it is Either.Right }) preferences.setHasCompletedPostAuthOnboarding(false)
    }

    private fun ownerFlow(): Flow<String> = combine(
        preferences.isUserLoggedIn,
        preferences.isGuestLoggedIn,
        preferences.userId
    ) { isUserLoggedIn, isGuestLoggedIn, userId ->
        userId?.takeIf { isUserLoggedIn == true && isGuestLoggedIn != true } ?: GUEST_OWNER
    }

    private suspend fun owner(): String = ownerFlow().first()

    private suspend fun call(request: suspend () -> KtorResponse<*>): Either<Failure, Unit> = try {
        if (request().isSuccessful) Either.Right(Unit) else Either.Left(Failure.ServerError)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Either.Left(Failure.NetworkConnection)
    }

    private suspend fun installationId(): String = installationIdProvider.get()

    private companion object {
        const val GUEST_OWNER = "guest"
        val LEGACY_SCREEN_QUESTIONS = listOf(OnboardingQuestion.EXPERIENCE)
    }
}

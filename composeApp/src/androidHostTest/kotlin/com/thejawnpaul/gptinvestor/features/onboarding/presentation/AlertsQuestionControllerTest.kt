package com.thejawnpaul.gptinvestor.features.onboarding.presentation

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.platform.NotificationPermissionController
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.AskTrigger
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestion
import com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases.GetQuestionToAskUseCase
import com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases.MarkQuestionAskedUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlertsQuestionControllerTest {

    private val getQuestionToAsk: GetQuestionToAskUseCase = mockk()
    private val markQuestionAsked: MarkQuestionAskedUseCase = mockk()
    private val preferences: AppPreferences = mockk(relaxed = true)
    private val notificationRepository: NotificationRepository = mockk(relaxed = true)
    private val notificationPermissionController: NotificationPermissionController = mockk()
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { preferences.notificationPermission } returns flowOf(null)
        every { preferences.isGuestLoggedIn } returns flowOf(false)
        coEvery { notificationPermissionController.areNotificationsEnabled() } returns false
        every { markQuestionAsked(any(), any()) } returns mockk(relaxed = true)
    }

    private fun TestScope.controller() = AlertsQuestionController(
        getQuestionToAsk,
        markQuestionAsked,
        preferences,
        notificationRepository,
        notificationPermissionController,
        analyticsLogger
    ).also { it.bind(this) }

    private fun questionToAsk(result: Either<Failure, OnboardingQuestion>) {
        coEvery { getQuestionToAsk.run(any()) } returns flowOf(result)
    }

    @Test
    fun `asks after an add and counts the question as asked`() = runTest {
        questionToAsk(Either.Right(OnboardingQuestion.ALERTS))
        val controller = controller()

        controller.onStockAdded("AAPL")
        advanceUntilIdle()

        assertThat(controller.state.value).isEqualTo(AlertsQuestionState(ticker = "AAPL"))
        verify { markQuestionAsked(OnboardingQuestion.ALERTS, any()) }
    }

    @Test
    fun `does not ask once notifications were allowed or refused`() = runTest {
        every { preferences.notificationPermission } returns flowOf(false)
        val controller = controller()

        controller.onStockAdded("AAPL")
        advanceUntilIdle()

        assertThat(controller.state.value).isEqualTo(AlertsQuestionState())
        coVerify(exactly = 0) { getQuestionToAsk.run(any()) }
    }

    @Test
    fun `does not ask when notifications are already on in the system`() = runTest {
        coEvery { notificationPermissionController.areNotificationsEnabled() } returns true
        val controller = controller()

        controller.onStockAdded("AAPL")
        advanceUntilIdle()

        assertThat(controller.state.value).isEqualTo(AlertsQuestionState())
        coVerify(exactly = 0) { getQuestionToAsk.run(any()) }
    }

    @Test
    fun `tells the question rules when the user is a guest`() = runTest {
        every { preferences.isGuestLoggedIn } returns flowOf(true)
        questionToAsk(Either.Left(Failure.NetworkConnection))
        val controller = controller()

        controller.onStockAdded("AAPL")
        advanceUntilIdle()

        coVerify { getQuestionToAsk.run(GetQuestionToAskUseCase.Params(AskTrigger.WATCHLIST_ADD, isGuest = true)) }
    }

    @Test
    fun `accepting requests the system permission and the result is saved`() = runTest {
        questionToAsk(Either.Right(OnboardingQuestion.ALERTS))
        val controller = controller()
        controller.onStockAdded("AAPL")
        advanceUntilIdle()

        controller.handleEvent(AlertsQuestionEvent.Accept)
        assertThat(controller.state.value).isEqualTo(AlertsQuestionState(requestPermission = true))

        controller.handleEvent(AlertsQuestionEvent.PermissionResult(granted = true))
        advanceUntilIdle()

        assertThat(controller.state.value).isEqualTo(AlertsQuestionState())
        coVerify { preferences.setNotificationPermission(true) }
        coVerify { notificationRepository.syncTokenIfNeeded() }
    }
}

package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authenticationRepository = mockk<AuthenticationRepository>(relaxed = true)
    private val preferences = mockk<AppPreferences>(relaxed = true)
    private val analyticsLogger = mockk<AnalyticsLogger>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(isGuest: Boolean): ProfileViewModel {
        every { preferences.userName } returns flowOf("John Doe")
        every { preferences.themePreference } returns flowOf("Light")
        every { preferences.isGuestLoggedIn } returns flowOf(isGuest)
        return ProfileViewModel(authenticationRepository, preferences, analyticsLogger)
    }

    @Test
    fun `should show toast instead of navigating when guest opens history`() = runTest {
        val viewModel = viewModel(isGuest = true)

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()
            viewModel.actions.test {
                viewModel.handleEvent(ProfileEvent.GoToHistory)
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isInstanceOf(ProfileAction.ShowToast::class.java)
            }
        }
    }

    @Test
    fun `should navigate to settings when signed in user opens settings`() = runTest {
        val viewModel = viewModel(isGuest = false)

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()
            viewModel.actions.test {
                viewModel.handleEvent(ProfileEvent.GoToSettings)
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isEqualTo(ProfileAction.OnGoToSettings)
            }
        }
    }

    @Test
    fun `should persist theme and log event when theme changed`() = runTest {
        val viewModel = viewModel(isGuest = false)

        viewModel.handleEvent(ProfileEvent.ChangeTheme("Dark"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferences.setThemePreference("Dark") }
        verify { analyticsLogger.logEvent("theme-changed", mapOf("theme" to "Dark")) }
    }

    @Test
    fun `should sign out when sign out clicked`() = runTest {
        val viewModel = viewModel(isGuest = false)

        viewModel.handleEvent(ProfileEvent.SignOut)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { authenticationRepository.signOut() }
    }
}

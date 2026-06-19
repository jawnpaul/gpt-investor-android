package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import com.thejawnpaul.gptinvestor.features.profile.presentation.state.ProfileUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val appPreferences: AppPreferences = mockk()
    private val authRepository: AuthenticationRepository = mockk()
    private val conversationRepository: IConversationRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = ProfileViewModel(appPreferences, authRepository, conversationRepository)

    @Test
    fun `state reflects userName and queryCount from their sources`() = runTest(testDispatcher) {
        every { appPreferences.userName } returns flowOf("Alice")
        every { conversationRepository.getQueryCount() } returns flowOf(7)

        val vm = buildViewModel()
        backgroundScope.launch { vm.state.collect { } }
        advanceUntilIdle()

        assertThat(vm.state.value.userName).isEqualTo("Alice")
        assertThat(vm.state.value.queryCount).isEqualTo(7)
        assertThat(vm.state.value.showSignOutConfirmation).isFalse()
    }

    @Test
    fun `SignOutClicked sets showSignOutConfirmation to true`() = runTest(testDispatcher) {
        every { appPreferences.userName } returns flowOf("")
        every { conversationRepository.getQueryCount() } returns flowOf(0)

        val vm = buildViewModel()
        backgroundScope.launch { vm.state.collect { } }
        advanceUntilIdle()

        vm.handleEvent(ProfileEvent.SignOutClicked)
        advanceUntilIdle()

        assertThat(vm.state.value.showSignOutConfirmation).isTrue()
    }

    @Test
    fun `DismissSignOutDialog resets showSignOutConfirmation to false`() = runTest(testDispatcher) {
        every { appPreferences.userName } returns flowOf("")
        every { conversationRepository.getQueryCount() } returns flowOf(0)

        val vm = buildViewModel()
        backgroundScope.launch { vm.state.collect { } }
        advanceUntilIdle()

        vm.handleEvent(ProfileEvent.SignOutClicked)
        advanceUntilIdle()
        assertThat(vm.state.value.showSignOutConfirmation).isTrue()

        vm.handleEvent(ProfileEvent.DismissSignOutDialog)
        advanceUntilIdle()
        assertThat(vm.state.value.showSignOutConfirmation).isFalse()
    }

    @Test
    fun `ConfirmSignOut dismisses dialog and calls signOut on repository`() = runTest(testDispatcher) {
        every { appPreferences.userName } returns flowOf("")
        every { conversationRepository.getQueryCount() } returns flowOf(0)
        coEvery { authRepository.signOut() } returns Result.success(Unit)

        val vm = buildViewModel()
        backgroundScope.launch { vm.state.collect { } }
        advanceUntilIdle()

        vm.handleEvent(ProfileEvent.SignOutClicked)
        advanceUntilIdle()

        vm.handleEvent(ProfileEvent.ConfirmSignOut)
        advanceUntilIdle()

        assertThat(vm.state.value.showSignOutConfirmation).isFalse()
        coVerify { authRepository.signOut() }
    }

    @Test
    fun `queryCount updates when repository emits a new value`() = runTest(testDispatcher) {
        val countFlow = MutableStateFlow(3)
        every { appPreferences.userName } returns flowOf("")
        every { conversationRepository.getQueryCount() } returns countFlow

        val vm = buildViewModel()
        backgroundScope.launch { vm.state.collect { } }
        advanceUntilIdle()

        assertThat(vm.state.value.queryCount).isEqualTo(3)

        countFlow.value = 10
        advanceUntilIdle()

        assertThat(vm.state.value.queryCount).isEqualTo(10)
    }
}

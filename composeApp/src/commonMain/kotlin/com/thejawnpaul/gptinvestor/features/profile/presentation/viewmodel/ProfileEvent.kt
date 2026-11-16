package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingExperience
import org.jetbrains.compose.resources.StringResource

sealed interface ProfileEvent {
    data object SignOutClicked : ProfileEvent
    data object ConfirmSignOut : ProfileEvent
    data object DismissSignOutDialog : ProfileEvent
    data object UpgradeToPremiumClicked : ProfileEvent
    data object HistoryClicked : ProfileEvent
    data object SavedPicksClicked : ProfileEvent
    data object SavedTidbitsClicked : ProfileEvent
    data object NotificationsClicked : ProfileEvent
    data object NotificationPermissionGranted : ProfileEvent
    data object NotificationPermissionDenied : ProfileEvent
    data object ScreenShown : ProfileEvent
    data object WatchingClicked : ProfileEvent
    data object AppearanceClicked : ProfileEvent
    data object PrivacyClicked : ProfileEvent
    data object SettingsClicked : ProfileEvent
    data object SignInClicked : ProfileEvent
    data object CreateAccountClicked : ProfileEvent
    data class ChangeTheme(val theme: StringResource) : ProfileEvent
    data object ExperienceClicked : ProfileEvent
    data object DismissExperienceSheet : ProfileEvent
    data class ExperienceSelected(val experience: InvestingExperience) : ProfileEvent
}

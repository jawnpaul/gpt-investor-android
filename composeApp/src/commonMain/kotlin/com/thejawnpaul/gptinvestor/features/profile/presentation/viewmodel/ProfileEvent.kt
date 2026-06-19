package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

sealed interface ProfileEvent {
    data object SignOutClicked : ProfileEvent
    data object ConfirmSignOut : ProfileEvent
    data object DismissSignOutDialog : ProfileEvent
    data object UpgradeToPremiumClicked : ProfileEvent
    data object HistoryClicked : ProfileEvent
    data object SavedPicksClicked : ProfileEvent
    data object SavedTidbitsClicked : ProfileEvent
    data object NotificationsClicked : ProfileEvent
    data object AppearanceClicked : ProfileEvent
    data object PrivacyClicked : ProfileEvent
}

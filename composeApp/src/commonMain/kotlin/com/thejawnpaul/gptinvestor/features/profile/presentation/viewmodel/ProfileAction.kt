package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

sealed interface ProfileAction {
    data object NavigateToHistory : ProfileAction
    data object NavigateToSavedPicks : ProfileAction
    data object NavigateToSavedTidbits : ProfileAction
    data object NavigateToWatchlist : ProfileAction
    data object ShowNotificationSettingsHint : ProfileAction
    data object NavigateToAppearance : ProfileAction
    data object NavigateToPrivacy : ProfileAction
    data object NavigateToUpgrade : ProfileAction
    data object NavigateToSettings : ProfileAction
    data object NavigateToSignUp : ProfileAction
    data object NavigateToLogin : ProfileAction
    data class ShowToast(val message: String) : ProfileAction
}

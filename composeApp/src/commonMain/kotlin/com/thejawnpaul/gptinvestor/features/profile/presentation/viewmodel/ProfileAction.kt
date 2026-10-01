package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

sealed interface ProfileAction {
    data object NavigateToHistory : ProfileAction
    data object NavigateToSavedPicks : ProfileAction
    data object NavigateToSavedTidbits : ProfileAction
    data object NavigateToNotifications : ProfileAction
    data object NavigateToAppearance : ProfileAction
    data object NavigateToPrivacy : ProfileAction
    data object NavigateToUpgrade : ProfileAction
    data object NavigateToSettings : ProfileAction
    data class ShowToast(val message: String) : ProfileAction
}

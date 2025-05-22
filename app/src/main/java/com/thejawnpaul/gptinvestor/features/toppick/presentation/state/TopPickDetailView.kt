package com.thejawnpaul.gptinvestor.features.toppick.presentation.state

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation

data class TopPickDetailView(
    val isLoggedIn: Boolean = false,
    val topPick: TopPickPresentation? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val companyPresentation: CompanyDetailRemoteResponse? = null,
    val showAuthenticateDialog: Boolean = false
)

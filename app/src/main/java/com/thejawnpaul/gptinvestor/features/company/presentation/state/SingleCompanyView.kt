package com.thejawnpaul.gptinvestor.features.company.presentation.state

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation

data class SingleCompanyView(
    val loading: Boolean = false,
    val company: CompanyDetailRemoteResponse? = null,
    val error: String? = null
)

package com.thejawnpaul.gptinvestor.features.news.domain

import com.thejawnpaul.gptinvestor.core.functional.Failure

sealed class NewsFailure : Failure.FeatureFailure() {
    data object NotFound : NewsFailure()
}

package com.thejawnpaul.gptinvestor.features.news.data

import com.thejawnpaul.gptinvestor.core.functional.Failure
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.serialization.ContentConvertException
import kotlinx.serialization.SerializationException

internal fun Exception.toNewsFailure(): Failure =
    if (this is ContentConvertException || this is SerializationException || this is NoTransformationFoundException) {
        Failure.DataError
    } else {
        Failure.NetworkConnection
    }

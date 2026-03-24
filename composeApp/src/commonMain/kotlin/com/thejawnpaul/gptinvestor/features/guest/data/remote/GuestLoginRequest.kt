package com.thejawnpaul.gptinvestor.features.guest.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuestLoginRequest(@SerialName("unique_id") val id: String)

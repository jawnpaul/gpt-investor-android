package com.thejawnpaul.gptinvestor.features.authentication.presentation

private val EMAIL_PATTERN = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

internal fun isValidEmail(email: String): Boolean = EMAIL_PATTERN.matches(email.trim())

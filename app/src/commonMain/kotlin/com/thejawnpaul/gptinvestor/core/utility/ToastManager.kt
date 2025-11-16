package com.thejawnpaul.gptinvestor.core.utility

expect open class ToastManager() {
    fun showToast(message: String, duration: ToastDuration)
}

enum class ToastDuration {
    Short, Long
}
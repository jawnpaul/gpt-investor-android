package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.annotation.Singleton

data class ToastMessage(
    val message: String,
    val icon: ImageVector? = null,
    val actionText: String? = null,
    val onAction: () -> Unit = {}
)

@Singleton
class ToastMessenger {
    private val _messages = Channel<ToastMessage>(Channel.BUFFERED)
    val messages: Flow<ToastMessage> = _messages.receiveAsFlow()

    fun show(message: String, icon: ImageVector? = null, actionText: String? = null, onAction: () -> Unit = {}) {
        _messages.trySend(ToastMessage(message, icon, actionText, onAction))
    }
}

package com.thejawnpaul.gptinvestor.features.conversation.domain.model

sealed interface AvailableModel {
    val isDefault: Boolean
    val modelId: String
    val modelTitle: String
    val modelSubtitle: String
    val canUpgrade: Boolean
    val isUserOnWaitlist: Boolean?
}

data class DefaultModel(
    override val isDefault: Boolean = true,
    override val modelId: String = "gemini-2.0-flash"
) : AvailableModel {
    override val modelTitle: String
        get() = "Quant Basic"
    override val modelSubtitle: String
        get() = "Good for everyday use"
    override val canUpgrade: Boolean
        get() = false
    override val isUserOnWaitlist: Boolean?
        get() = null
}

data class AnotherModel(
    override val isDefault: Boolean = false,
    override val modelTitle: String,
    override val modelSubtitle: String,
    override val canUpgrade: Boolean,
    override val isUserOnWaitlist: Boolean?,
    override val modelId: String
) : AvailableModel

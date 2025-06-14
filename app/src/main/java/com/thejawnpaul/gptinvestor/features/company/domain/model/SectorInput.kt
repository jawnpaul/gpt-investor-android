package com.thejawnpaul.gptinvestor.features.company.domain.model

sealed interface SectorInput {
    val hasImage: Boolean

    data object AllSector : SectorInput {
        override val hasImage: Boolean
            get() = false
    }

    data class CustomSector(
        val sectorName: String,
        val sectorKey: String,
        override val hasImage: Boolean = false
    ) : SectorInput
}

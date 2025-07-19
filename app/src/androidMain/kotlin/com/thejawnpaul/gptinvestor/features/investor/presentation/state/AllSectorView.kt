package com.thejawnpaul.gptinvestor.features.investor.presentation.state

import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput

data class AllSectorView(
    val sectors: List<SectorInput> = emptyList(),
    val selected: SectorInput = SectorInput.AllSector
) {
    val searchPlaceHolder = when (selected) {
        is SectorInput.AllSector -> {
            "Search All Companies"
        }
        is SectorInput.CustomSector -> {
            "Search ${selected.sectorName} Companies"
        }
    }
}

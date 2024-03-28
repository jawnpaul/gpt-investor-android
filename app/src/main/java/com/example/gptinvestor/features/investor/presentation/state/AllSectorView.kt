package com.example.gptinvestor.features.investor.presentation.state

import com.example.gptinvestor.features.company.domain.model.SectorInput

data class AllSectorView(
    val sectors: List<SectorInput> = emptyList(),
    val selected: SectorInput = SectorInput.AllSector
)

package com.example.gptinvestor.features.company.domain.model

sealed class SectorInput {
    data object AllSector : SectorInput()
    data class CustomSector(val sectorName: String, val sectorKey: String) : SectorInput()
}

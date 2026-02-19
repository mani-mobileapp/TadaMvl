package com.tada.mvl.data.model

data class ReverseResponse(
    val localityInfo: LocalityInfo
)

data class LocalityInfo(
    val administrative: List<AdministrativeEntry>,
    val informative: List<Any>? = null
)

data class AdministrativeEntry(
    val order: Int,
    val name: String
)

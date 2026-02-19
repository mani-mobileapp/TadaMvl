package com.tada.mvl.data.model

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val aqi: Int,
    val name: String,
    var nickname: String? = null
)
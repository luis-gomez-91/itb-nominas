package org.itb.nominas.core.domain

data class LocationItem(
    val latitude: Double,
    val longitude: Double,
    val countryCode: String = "EC"
)

package org.itb.nominas.features.tracker.data

import kotlinx.serialization.Serializable

@Serializable
data class TrackerRequest(
    val clientAddress: String,
    val latitud: Double,
    val longitud: Double,
    val idPersonal: Int
)
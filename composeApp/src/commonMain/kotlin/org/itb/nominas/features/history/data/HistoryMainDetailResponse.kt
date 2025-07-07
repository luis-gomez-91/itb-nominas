package org.itb.nominas.features.history.data
import kotlinx.serialization.Serializable

@Serializable
data class HistoryMainDetailResponse(
    val hora: String,
    val latitud: Double?,
    val longitud: Double?
)
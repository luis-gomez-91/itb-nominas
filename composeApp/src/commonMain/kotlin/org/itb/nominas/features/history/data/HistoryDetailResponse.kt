package org.itb.nominas.features.history.data

import kotlinx.serialization.Serializable

@Serializable
data class HistoryDetailResponse(
    val fecha: String?,
    val hora: String?,
    val tipo: String,
    val ip: String?,
    val latitud: Double?,
    val longitud: Double?,
    val observacion: String?,
    val evidencia: String?,
    val nom_archevidencia: String?,
    val isSalida: Boolean
)
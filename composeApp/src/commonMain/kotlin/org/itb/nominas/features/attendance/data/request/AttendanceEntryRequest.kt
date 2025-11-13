package org.itb.nominas.features.attendance.data.request

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceEntryRequest(
    val comment: String?,
    val clientAddress: String,
    val latitude: Double,
    val longitude: Double,
    val idMotivoSalida: Int?,
    val userAgent: String?
)

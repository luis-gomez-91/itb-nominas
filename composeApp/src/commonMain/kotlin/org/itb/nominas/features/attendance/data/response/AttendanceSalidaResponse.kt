package org.itb.nominas.features.attendance.data.response

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceSalidaResponse(
    val id: Int,
    val descripcion: String,
    val isAlmuerzo: Boolean
)

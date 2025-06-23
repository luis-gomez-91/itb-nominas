package org.itb.nominas.features.attendance.data

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRegistroResponse(
    val comentario: String?,
    val fecha: String,
    val hora: String,
    val tipo: String
)
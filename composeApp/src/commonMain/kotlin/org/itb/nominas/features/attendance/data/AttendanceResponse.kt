package org.itb.nominas.features.attendance.data

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceResponse(
    val debeMarcarSalida: Boolean,
    val marcacionActual: String?,
    val registros: List<AttenadnceRegistroResponse>?
)
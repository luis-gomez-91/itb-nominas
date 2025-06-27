package org.itb.nominas.features.attendance.data.response

import kotlinx.serialization.Serializable


@Serializable
data class AttendanceResponse(
    val debeMarcarSalida: Boolean,
    val marcacionActual: String?,
    val registros: List<AttendanceRegistroResponse>?,
    val ultimoRegistro: AttendanceUltimoRegistroResponse?,
    val motivosSalida: List<AttendanceSalidaResponse>
)
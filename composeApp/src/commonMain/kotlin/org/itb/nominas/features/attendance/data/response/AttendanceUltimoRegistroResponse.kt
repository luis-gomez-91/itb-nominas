package org.itb.nominas.features.attendance.data.response

import kotlinx.serialization.Serializable
import org.itb.nominas.core.data.response.MotivoSalidaResponse

@Serializable
data class AttendanceUltimoRegistroResponse(
    val isSalida: Boolean,
    val motivo: List<MotivoSalidaResponse>
)

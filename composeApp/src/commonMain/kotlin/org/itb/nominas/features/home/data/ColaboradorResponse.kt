package org.itb.nominas.features.home.data

import kotlinx.serialization.Serializable
import org.itb.nominas.features.attendance.data.response.AttendanceSalidaResponse
import org.itb.nominas.features.attendance.data.response.AttendanceUltimoRegistroResponse

@Serializable
data class ColaboradorResponse(
    val area: String,
    val decimo_cuarto: String,
    val decimo_tercero: String,
    val fechaAfiliacion: String,
    val fondo_reserva: String,
    val foto: String?,
    val id: Int,
    val last_conection_date: String?,
    val last_conection_time: String?,
    val nombre: String,
    val sexo: String,
    val urlSistema: String,
    val nombreSistema: String,
    val username: String,
    val qr_url: String,
    var ultimoRegistro: AttendanceUltimoRegistroResponse?,
    val motivosSalida: List<AttendanceSalidaResponse>
)
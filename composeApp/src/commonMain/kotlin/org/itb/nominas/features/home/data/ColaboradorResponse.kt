package org.itb.nominas.features.home.data

import kotlinx.serialization.Serializable

@Serializable
data class ColaboradorResponse(
    val area: String,
    val decimo_cuarto: String,
    val decimo_tercero: String,
    val fechaAfiliacion: String,
    val fondo_reserva: String,
    val foto: String,
    val id: Int,
    val last_conection_date: String,
    val last_conection_time: String,
    val nombre: String,
    val sexo: String,
    val urlSistema: String,
    val nombreSistema: String,
    val username: String
)
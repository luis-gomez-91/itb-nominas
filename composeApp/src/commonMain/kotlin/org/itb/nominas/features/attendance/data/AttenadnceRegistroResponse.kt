package org.itb.nominas.features.attendance.data

import kotlinx.serialization.Serializable

@Serializable
data class AttenadnceRegistroResponse(
    val comentario: String,
    val fecha: String,
    val hora: String,
    val tipo: String
)
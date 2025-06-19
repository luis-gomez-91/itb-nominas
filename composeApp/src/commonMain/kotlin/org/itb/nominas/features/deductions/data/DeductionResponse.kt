package org.itb.nominas.features.deductions.data

import kotlinx.serialization.Serializable

@Serializable
data class DeductionResponse(
    val cancelado: Boolean,
    val fecha: String,
    val motivo: String,
    val numCuota: String,
    val tipo: String,
    val valor: Double
)
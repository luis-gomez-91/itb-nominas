package org.itb.nominas.features.payroll.data

import kotlinx.serialization.Serializable

@Serializable
data class PayRollDeduction(
    val adicional: String? = null,
    val descripcion: String,
    val valor: String
)
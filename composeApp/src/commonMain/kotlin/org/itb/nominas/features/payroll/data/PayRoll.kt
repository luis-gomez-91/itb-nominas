package org.itb.nominas.features.payroll.data

import kotlinx.serialization.Serializable

@Serializable
data class PayRoll(
    val beneficiosextra: Double,
    val descuento: Double,
    val detailDescuentos: List<PayRollDeduction> = emptyList<PayRollDeduction>(),
    val fecha: String,
    val id: Int,
    val idPersona: Int,
    val idRol: Int,
    val nombre: String,
    val quincena: Double,
    val reportName: String? = null,
    val revisado: Boolean,
    val sueldo: Double,
    val total: Double,
    val fdm: Double,
    val totalDescuentos: Double
)
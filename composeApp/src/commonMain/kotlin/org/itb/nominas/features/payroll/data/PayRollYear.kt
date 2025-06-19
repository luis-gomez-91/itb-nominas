package org.itb.nominas.features.payroll.data

import kotlinx.serialization.Serializable

@Serializable
data class PayRollYear(
    val rolPago: List<PayRoll> = emptyList<PayRoll>(),
    val year: Int
)
package org.itb.nominas.features.tracker.data

import kotlinx.serialization.Serializable

@Serializable
data class TrackerItemResponse(
    val id: Int,
    val nombreColaborador: String,
    val fechaIngreso: String,
    val horaIngreso: String,
    val fechaSalida: String?,
    val horaSalida: String?
)
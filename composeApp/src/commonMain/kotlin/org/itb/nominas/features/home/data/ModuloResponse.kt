package org.itb.nominas.features.home.data

import kotlinx.serialization.Serializable

@Serializable
data class ModuloResponse(
    val descripcion: String,
    val id: Int,
    val imagen: String? = null,
    val nombre: String,
    val url: String
)
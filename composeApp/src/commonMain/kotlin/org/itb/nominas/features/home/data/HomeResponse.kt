package org.itb.nominas.features.home.data

import kotlinx.serialization.Serializable

@Serializable
data class HomeResponse(
    val colaborador: ColaboradorResponse,
    val modulos: List<ModuloResponse>
)
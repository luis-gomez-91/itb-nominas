package org.itb.nominas.core.data.response

import kotlinx.serialization.Serializable

@Serializable
data class MotivoSalidaResponse(
    val id: Int,
    val descripcion:String
)

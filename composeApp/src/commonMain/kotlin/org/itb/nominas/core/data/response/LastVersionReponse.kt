package org.itb.nominas.core.data.response

import kotlinx.serialization.Serializable

@Serializable
data class LastVersionReponse(
    val version: Int
)

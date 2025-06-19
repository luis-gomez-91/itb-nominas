package org.itb.nominas.core.data.response

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse (
    val message: String
)
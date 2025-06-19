package org.itb.nominas.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest (
    val refresh: String
)
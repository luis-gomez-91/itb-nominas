package org.itb.nominas.features.login.data

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val refresh: String,
    val access: String
)

@Serializable
data class AccessTokenResponse(
    val access: String
)
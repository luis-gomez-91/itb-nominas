package org.itb.nominas.features.login.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val username: String,
    val nombre: String,
    val correo: String,
    val sistema: String,
    val tokens: TokenResponse
)
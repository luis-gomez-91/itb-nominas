package org.itb.nominas.features.login.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val access: String,
    val refresh: String,
    val user: UserInfo? = null
)

@Serializable
data class UserInfo(
    val username: String,
    val nombre: String,
    val correo: String,
    val sistema: String
)
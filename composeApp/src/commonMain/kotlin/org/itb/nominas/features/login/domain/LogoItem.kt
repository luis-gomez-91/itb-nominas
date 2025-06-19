package org.itb.nominas.features.login.domain

import org.jetbrains.compose.resources.DrawableResource

data class LogoItem(
    val description: String,
    val url: String? = null,
    val resource: DrawableResource
)
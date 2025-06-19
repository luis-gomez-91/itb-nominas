package org.itb.nominas.core.domain

import androidx.compose.ui.graphics.vector.ImageVector

data class MainDrawerItem(
    val label: String,
    val icon: ImageVector,
    val onclick: () -> Unit
)
package org.itb.nominas.core.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle

data class BottomBarItem (
    val onclick: () -> Unit,
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val style: TextStyle
)
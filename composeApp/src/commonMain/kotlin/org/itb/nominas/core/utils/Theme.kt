package org.itb.nominas.core.utils

import androidx.compose.runtime.Composable
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Moon
import compose.icons.evaicons.outline.Smartphone
import compose.icons.evaicons.outline.Sun
import org.itb.nominas.core.domain.ThemeItem

enum class Theme {
    Light,
    Dark,
    SystemDefault
}

@Composable
fun Theme.getTheme() : ThemeItem {
    return when (this) {
        Theme.Light -> ThemeItem("Modo Claro", EvaIcons.Outline.Sun)
        Theme.Dark -> ThemeItem("Modo Oscuro", EvaIcons.Outline.Moon)
        Theme.SystemDefault -> ThemeItem("Defecto del Sistema", EvaIcons.Outline.Smartphone)
    }
}

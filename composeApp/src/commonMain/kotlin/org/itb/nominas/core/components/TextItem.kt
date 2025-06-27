package org.itb.nominas.core.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TextItem(
    title: String,
    descripcion: String
) {
    Text(
        text = TextFormat("$title: ", descripcion),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
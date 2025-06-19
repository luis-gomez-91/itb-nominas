package org.itb.nominas.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MyCard(
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit = {},
    borderColor : Color? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit = {}
) {
    Card (
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        onClick = { onClick() },
        border = borderColor?.let { BorderStroke(2.dp, it) }
    ) {
        Box (
            modifier = modifier
                .padding(8.dp)
        ) {
            content()
        }
    }
}
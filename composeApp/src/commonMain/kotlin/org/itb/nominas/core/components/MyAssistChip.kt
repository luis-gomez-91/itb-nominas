package org.itb.nominas.core.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun MyAssistChip(
    label: String,
    containerColor: Color,
    labelColor: Color,
    icon: ImageVector? = null,
    onClick: () -> Unit = {},
    height: Dp? = 24.dp // Ahora es opcional
) {
    val modifier = if (height != null) {
        Modifier
            .height(height)
            .padding(0.dp)
    } else {
        Modifier
            .padding(0.dp) // Solo padding si no hay altura fija
    }

    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = labelColor
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = labelColor
        ),
        border = null,
        leadingIcon = {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = "Icon",
                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                    tint = labelColor
                )
            }
        },
        modifier = modifier
    )
}

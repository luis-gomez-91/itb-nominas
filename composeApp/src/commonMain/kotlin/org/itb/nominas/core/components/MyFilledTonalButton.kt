package org.itb.nominas.core.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import io.github.aakira.napier.Napier

@Composable
fun MyFilledTonalButton(
    text: String,
    enabled: Boolean = true,
    onClickAction: () -> Unit,
    icon: ImageVector? = null,
    buttonColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.primary,
    iconSize: Dp = 24.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MaterialTheme.shapes.small
) {
    FilledTonalButton(
        onClick = {
            Napier.i("🎯 onClick de FilledTonalButton ejecutado", tag = "MyFilledTonalButton")
            onClickAction()
        },
        enabled = enabled,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = buttonColor,
            contentColor = if (enabled) textColor else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier,
        shape = shape
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(iconSize),
                tint = if (enabled) textColor else MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = textStyle,
            color = if (enabled) textColor else MaterialTheme.colorScheme.outline
        )
    }
}
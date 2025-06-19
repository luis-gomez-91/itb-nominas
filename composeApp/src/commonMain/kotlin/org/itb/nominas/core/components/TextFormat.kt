package org.itb.nominas.core.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString

@Composable
fun TextFormat(
    title: String,
    description: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
    descriptionStyle: TextStyle = MaterialTheme.typography.bodySmall
): AnnotatedString {
    return buildAnnotatedString {
        append(
            AnnotatedString(
                text = title,
                spanStyle = titleStyle.toSpanStyle()
            )
        )
        append(" ")
        append(
            AnnotatedString(
                text = description,
                spanStyle = descriptionStyle.toSpanStyle()
            )
        )
    }
}
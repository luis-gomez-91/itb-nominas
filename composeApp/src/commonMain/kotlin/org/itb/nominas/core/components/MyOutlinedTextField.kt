package org.itb.nominas.core.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun MyOutlinedTextField(
    value: String,
    placeholder: String = "",
    label: String? = null,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onFocusLost: () -> Unit = {},
    textStyle: TextStyle = TextStyle(
        color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
    )
) {
    val focusRequester = remember { FocusRequester() }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    onFocusLost()
                }
            },
        placeholder = { Text(text = placeholder) },
        label = {
            label?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                )
            }
        },
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 1f),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        enabled = enabled,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation
    )
}
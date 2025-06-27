package org.itb.nominas.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MyExposedDropdownMenuBox(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    label: String,
    selectedOption: T?,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    getOptionDescription: (T) -> String,
    enabled: Boolean = true,
    onSearchTextChange: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(selectedOption) {
        searchText = selectedOption?.let { getOptionDescription(it) } ?: ""
    }

    val shape = RoundedCornerShape(4.dp)

    ExposedDropdownMenuBox(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                color = if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                shape = shape
            ),
        expanded = expanded,
        onExpandedChange = { if (enabled) onExpandedChange(it) }
    ) {
        TextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                onSearchTextChange(newText) // Mantener el texto actualizado
            },
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (enabled) {
                            onExpandedChange(!expanded)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            textStyle = MaterialTheme.typography.labelSmall,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 1f),
                disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable(enabled)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onExpandedChange(true) // Expande el menú solo si el campo tiene foco
                    }
                },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            singleLine = true,
            interactionSource = interactionSource
        )

        // Mostrar el menú desplegable solo si hay opciones
        if (options.isNotEmpty()) {
            ExposedDropdownMenu(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = getOptionDescription(option),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = {
                            searchText = getOptionDescription(option)
                            onOptionSelected(option)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

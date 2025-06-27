package org.itb.nominas.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Close
import compose.icons.evaicons.outline.Menu
import compose.icons.evaicons.outline.Search
import kotlinx.coroutines.launch
import org.itb.nominas.core.utils.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    mainViewModel: MainViewModel,
    drawerState: DrawerState,
) {
    val focusRequester = remember { FocusRequester() }
    var onSearch by remember { mutableStateOf(true) }
    val title by mainViewModel.title.collectAsState(null)
    val scope = rememberCoroutineScope()

    if (!onSearch) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Crossfade(targetState = onSearch) { isSearch ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (!isSearch) {
                            var searchQuery by remember { mutableStateOf("") }

                            TextField(
                                value = searchQuery,
                                onValueChange = { newQuery ->
                                    searchQuery = newQuery
                                },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                label = { Text("Buscar") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search,
                                    keyboardType = KeyboardType.Text
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        mainViewModel.setSearchQuery(searchQuery)
                                    }
                                )
                            )
                        } else {
                            Text(
                                text = title?: "",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                }
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        actions = {
            AnimatedContent(targetState = onSearch) { isSearch ->
                IconButton(
                    onClick = {
                        onSearch = !onSearch
                        mainViewModel.setSearchQuery(null)
                    }
                ) {
                    Icon(
                        imageVector = if (isSearch) EvaIcons.Outline.Search else EvaIcons.Outline.Close,
                        contentDescription = if (isSearch) "Activar búsqueda" else "Desactivar búsqueda",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

        },
        navigationIcon = {
            IconButton(
                onClick = { scope.launch { drawerState.open() } }
            ) {
                Icon(
                    imageVector = EvaIcons.Outline.Menu,
                    contentDescription = "Menú lateral",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },

    )
}


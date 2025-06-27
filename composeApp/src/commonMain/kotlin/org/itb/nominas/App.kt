package org.itb.nominas

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nominas.composeapp.generated.resources.Res
import nominas.composeapp.generated.resources.logo
import nominas.composeapp.generated.resources.logo_dark
import org.itb.nominas.core.components.MyFilledTonalButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.itb.nominas.core.theme.AppTheme
import org.itb.nominas.core.navigation.NavigationWrapper
import org.itb.nominas.core.platform.appIsLastVersion
import org.itb.nominas.core.platform.openPlayStoreOrAppStore
import org.itb.nominas.core.utils.BiometryViewModel
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.core.utils.Theme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App(
    biometryViewModel: BiometryViewModel
) {
    val mainViewModel: MainViewModel = koinViewModel()
    val themeSelect by mainViewModel.selectedTheme.collectAsState()
    val appLastVersion by mainViewModel.appLastVersion.collectAsState(null)

    LaunchedEffect(Unit) {
        mainViewModel.fetchLastVersionApp()
    }

    AppTheme(
        theme = themeSelect
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                appLastVersion?.let {
                    if (appIsLastVersion(it.version)) {
                        NavigationWrapper(biometryViewModel)
                    } else {
                        val logo = when (themeSelect) {
                            Theme.Dark -> Res.drawable.logo_dark
                            Theme.Light -> Res.drawable.logo
                            Theme.SystemDefault -> { if (isSystemInDarkTheme()) Res.drawable.logo_dark else Res.drawable.logo }
                        }

                        Column (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 0.dp, start = 32.dp, end = 32.dp, bottom = 64.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(logo),
                                contentDescription = "logo",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "¡Una nueva versión está disponible! Actualiza la app para seguir disfrutando de las mejores funciones.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            MyFilledTonalButton(
                                text = "Actualizar",
                                buttonColor = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.primary,
                                icon = Icons.Filled.Update,
                                onClickAction = { openPlayStoreOrAppStore() },
                                iconSize = 32.dp,
                                textStyle = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

    }
}
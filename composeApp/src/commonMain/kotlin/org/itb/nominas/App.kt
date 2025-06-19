package org.itb.nominas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.itb.nominas.core.theme.AppTheme
import org.itb.nominas.core.navigation.NavigationWrapper
import org.itb.nominas.core.utils.BiometryViewModel
import org.itb.nominas.core.utils.MainViewModel
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
                NavigationWrapper(biometryViewModel)
            }
        }

    }
}
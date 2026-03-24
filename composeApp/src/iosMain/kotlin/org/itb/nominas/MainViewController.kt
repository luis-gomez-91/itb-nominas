package org.itb.nominas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import dev.icerock.moko.biometry.compose.BindBiometryAuthenticatorEffect
import dev.icerock.moko.biometry.compose.rememberBiometryAuthenticatorFactory
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import org.itb.nominas.core.di.initKoin
import org.itb.nominas.core.utils.BiometryViewModel
import org.itb.nominas.core.utils.MainViewModel
import org.koin.compose.koinInject
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController {
        IOSApp()
    }
}

@Composable
private fun IOSApp() {
    // BiometryAuthenticator
    val biometryFactory = rememberBiometryAuthenticatorFactory()
    val biometryAuthenticator = biometryFactory.createBiometryAuthenticator()
    BindBiometryAuthenticatorEffect(biometryAuthenticator)

    val biometryViewModel = BiometryViewModel(biometryAuthenticator)

    // PermissionsController para iOS
    val permissionsFactory = rememberPermissionsControllerFactory()
    val permissionsController = permissionsFactory.createPermissionsController()
    BindEffect(permissionsController)

    // Obtener MainViewModel de Koin
    val mainViewModel: MainViewModel = koinInject()

    // IMPORTANTE: Asignar el PermissionsController manualmente en iOS
    LaunchedEffect(permissionsController) {
        // Crear una propiedad mutable en MainViewModel
        // O pasar el permissionsController donde lo necesites
    }

    App(biometryViewModel)
}
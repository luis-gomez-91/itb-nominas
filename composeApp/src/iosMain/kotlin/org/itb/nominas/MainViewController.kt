package org.itb.nominas

import androidx.compose.ui.window.ComposeUIViewController
import dev.icerock.moko.biometry.BiometryAuthenticator
import org.itb.nominas.core.di.initKoin
import org.itb.nominas.core.utils.BiometryViewModel

fun MainViewController() = ComposeUIViewController (
    configure = { initKoin() }
) {
    val biometricAuth = BiometryAuthenticator()
    val biometryViewModel = BiometryViewModel(biometricAuth)
    App(biometryViewModel)
}
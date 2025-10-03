package org.itb.nominas

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import dev.icerock.moko.biometry.compose.BindBiometryAuthenticatorEffect
import dev.icerock.moko.biometry.compose.BiometryAuthenticatorFactory
import dev.icerock.moko.biometry.compose.rememberBiometryAuthenticatorFactory
import dev.icerock.moko.mvvm.getViewModel
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import org.itb.nominas.core.utils.BiometryViewModel
import org.koin.compose.getKoin


class MainActivity : AppCompatActivity() {
    private lateinit var biometryViewModel: BiometryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val biometryFactory: BiometryAuthenticatorFactory = rememberBiometryAuthenticatorFactory()

            biometryViewModel = getViewModel {
                BiometryViewModel(
                    biometryAuthenticator = biometryFactory.createBiometryAuthenticator()
                )
            }
            BindBiometryAuthenticatorEffect(biometryViewModel.biometryAuthenticator)

            val permissionsController: PermissionsController = getKoin().get()
            BindEffect(permissionsController)
            App(biometryViewModel)
        }
    }
}
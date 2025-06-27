package org.itb.nominas.features.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import compose.icons.EvaIcons
import compose.icons.TablerIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Eye
import compose.icons.evaicons.outline.EyeOff
import compose.icons.evaicons.outline.LogIn
import compose.icons.tablericons.FaceId
import compose.icons.tablericons.Fingerprint
import nominas.composeapp.generated.resources.Res
import nominas.composeapp.generated.resources.conduce
import nominas.composeapp.generated.resources.continua
import nominas.composeapp.generated.resources.logo
import nominas.composeapp.generated.resources.logo_dark
import nominas.composeapp.generated.resources.online
import org.itb.nominas.core.components.MyErrorAlert
import org.itb.nominas.features.login.domain.LogoItem
import org.itb.nominas.core.components.MyFilledTonalButton
import org.itb.nominas.core.components.MyOutlinedTextField
import org.itb.nominas.core.platform.getPlatform
import org.itb.nominas.features.login.ui.domain.DrawerItem
import org.itb.nominas.core.components.FullScreenLoading
import org.itb.nominas.core.utils.BiometryViewModel
import org.itb.nominas.core.utils.Theme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun LoginScreen (
    navHostController: NavHostController,
    biometryViewModel: BiometryViewModel,
    loginViewModel: LoginViewModel = koinViewModel()
) {

    val logos = listOf(
        LogoItem(description = "Online", resource = Res.drawable.online),
        LogoItem(description = "Conducción", resource = Res.drawable.conduce),
        LogoItem(description = "Continua", resource = Res.drawable.continua)
    )

    val username by loginViewModel.username.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val verPassword by loginViewModel.verPassword.collectAsState(false)
    val isLoading by loginViewModel.isLoading.collectAsState(false)
    val error by loginViewModel.error.collectAsState(null)
    val themeSelect by loginViewModel.mainViewModel.selectedTheme.collectAsState()

    val logo = when (themeSelect) {
        Theme.Dark -> Res.drawable.logo_dark
        Theme.Light -> Res.drawable.logo
        Theme.SystemDefault -> { if (isSystemInDarkTheme()) Res.drawable.logo_dark else Res.drawable.logo }
    }

    Box {
        FullScreenLoading(isLoading = isLoading)

        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween

        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .safeContentPadding()
                    .padding(start = 32.dp, end = 32.dp, bottom = 64.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(logo),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxWidth(1f),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(32.dp))

                MyOutlinedTextField(
                    value = username,
                    onValueChange = { loginViewModel.onLoginChanged(it, password) },
                    placeholder = "Usuario",
                    label = "Usuario",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                MyOutlinedTextField(
                    value = password,
                    onValueChange = { loginViewModel.onLoginChanged(username, it) },
                    placeholder = "Contraseña",
                    label = "Contraseña",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if(!verPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        if (password.isNotBlank()) {
                            PasswordIcon(
                                isPasswordVisible = verPassword,
                                onIconClick = { loginViewModel.togglePasswordVisibility() }
                            )
                        }
                    }
                )
                val drawerItem = when(getPlatform().name) {
                    "Android" -> DrawerItem("Ingresar con huella o Face ID", TablerIcons.Fingerprint)
                    "iOS" -> DrawerItem("Ingresar con Face ID o Touch ID", TablerIcons.FaceId)
                    else -> DrawerItem("Ingresar con credenciales biométricas", TablerIcons.Fingerprint)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    TextButton(
                        onClick = {
                            biometryViewModel.auth(navHostController)
                        }
                    ) {
                        Text(
                            text = drawerItem.message,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = drawerItem.icon,
                            contentDescription = "Biométrico",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                MyFilledTonalButton(
                    text = "Ingresar",
                    enabled = loginViewModel.habilitaBoton(),
                    icon = EvaIcons.Outline.LogIn,
                    iconSize = 24.dp,
                    buttonColor = MaterialTheme.colorScheme.primaryContainer,
                    textColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    textStyle = MaterialTheme.typography.titleMedium,
                    onClickAction = {
                        loginViewModel.onLoginSelector(navHostController)
                    }
                )
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                val imageWidth = (maxWidth - (32.dp * (logos.size - 1)) - 64.dp) / logos.size
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    items(logos) { logo ->
                        Image(
                            painter = painterResource(logo.resource),
                            contentDescription = logo.description,
                            modifier = Modifier
                                .width(imageWidth)
                                .aspectRatio(1f)
                                .height(100.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }

    error?.let {
        MyErrorAlert(
            titulo = "Error",
            mensaje = it.message,
            onDismiss = {
                loginViewModel.clearError()
            },
            showAlert = true
        )
    }
}

@Composable
fun PasswordIcon(
    isPasswordVisible: Boolean,
    onIconClick: () -> Unit
) {
    val image = if (isPasswordVisible) EvaIcons.Outline.EyeOff else EvaIcons.Outline.Eye
    val description = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"

    IconButton(onClick = { onIconClick() }) {
        Icon(imageVector = image, contentDescription = description)
    }
}

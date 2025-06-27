package org.itb.nominas.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import compose.icons.EvaIcons
import compose.icons.TablerIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Home
import compose.icons.evaicons.fill.LogOut
import compose.icons.evaicons.fill.Person
import compose.icons.evaicons.outline.Home
import compose.icons.evaicons.outline.Person
import compose.icons.tablericons.Qrcode
import org.itb.nominas.core.domain.BottomBarItem
import org.itb.nominas.core.navigation.HomeRoute
import org.itb.nominas.core.navigation.ProfileRoute
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.home.data.ColaboradorResponse
import org.itb.nominas.features.home.ui.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun MainBottomBar(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomSheetProfile by mainViewModel.bottomSheetProfile.collectAsState(false)
    val bottomSheetQR by mainViewModel.bottomSheetQR.collectAsState(false)
    val isHomeSelected = currentRoute == HomeRoute::class.qualifiedName
    val isProfileSelected = currentRoute == ProfileRoute::class.qualifiedName
    val colaborador by mainViewModel.colaborador.collectAsState(null)

    val navigationIcons = listOf<BottomBarItem>(
        BottomBarItem(
            onclick = { mainViewModel.setBottomSheetQR(true) },
            label = "QR Code",
            icon = TablerIcons.Qrcode,
            color = if (isProfileSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            style = if (isProfileSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            isSelected = isProfileSelected
        ),
        BottomBarItem(
            onclick = { navController.navigate(HomeRoute) },
            label = "Inicio",
            icon = if (isHomeSelected) EvaIcons.Fill.Home else EvaIcons.Outline.Home,
            color = if (isHomeSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            style = if (isHomeSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            isSelected = isHomeSelected
        ),
        BottomBarItem(
            onclick = { mainViewModel.setBottomSheetProfile(true) },
            label = "Perfil",
            icon = if (isProfileSelected) EvaIcons.Fill.Person else EvaIcons.Outline.Person,
            color = if (isProfileSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            style = if (isProfileSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            isSelected = isProfileSelected
        )
    )

    Surface (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
    ){
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            navigationIcons.forEach {
                val animatedIconSize by animateDpAsState(
                    targetValue = if (it.isSelected) 28.dp else 24.dp,
                    animationSpec = tween(durationMillis = 300)
                )

                val animatedColor by animateColorAsState(
                    targetValue = it.color,
                    animationSpec = tween(durationMillis = 300)
                )

                NavigationBarItem(
                    selected = it.isSelected,
                    onClick = { it.onclick() },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.label,
                            tint = animatedColor,
                            modifier = Modifier.size(animatedIconSize)
                        )
                    },
                    label = {
                        Text(
                            text = it.label,
                            style = it.style,
                            color = animatedColor
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = animatedColor,
                        unselectedIconColor = animatedColor,
                        selectedTextColor = animatedColor,
                        unselectedTextColor = animatedColor
                    )
                )
            }
        }
    }

    colaborador?.let {
        val homeViewModel: HomeViewModel = koinViewModel()
        if (bottomSheetProfile) {
            BottomSheetProfile(
                colaborador = it,
                onDismiss = {
                    mainViewModel.setBottomSheetProfile(false)
                },
                onclick = {
                    homeViewModel.clearData()
                    mainViewModel.logout(navController)
                }
            )
        }

        if (bottomSheetQR) {
            BottomSheetQR(
                qr = it.qr_url,
                onDismiss = {
                    mainViewModel.setBottomSheetQR(false)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetQR(
    qr: String,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Código QR",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            AsyncImage(
                model = qr,
                contentDescription = "QR Code",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetProfile(
    colaborador: ColaboradorResponse,
    onDismiss: () -> Unit,
    onclick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val finalPhoto = colaborador.foto ?: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQTz02QJMGkQbLyOApa3_ZDRqr_QiGJh120ZQ&s"

            AsyncImage(
                model = finalPhoto,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = colaborador.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = TextFormat("Última conexión:", if (colaborador.last_conection_date != null) "${colaborador.last_conection_date} ${colaborador.last_conection_time}" else "sin coincidencias"),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                TextItem("Usuario", colaborador.username)
                TextItem("Sistema", "${colaborador.nombreSistema} (${colaborador.urlSistema})")
                TextItem("Departamento", colaborador.area)
                TextItem("Fecha de afiliación", colaborador.fechaAfiliacion)
                TextItem("Décimo tercero", colaborador.decimo_tercero)
                TextItem("Décimo cuarto", colaborador.decimo_cuarto)
                TextItem("Fondos de reserva", colaborador.fondo_reserva)
            }
            Spacer(Modifier.height(16.dp))

            MyFilledTonalButton(
                text = "Cerrar sesión",
                enabled = true,
                icon = EvaIcons.Fill.LogOut,
                iconSize = 24.dp,
                buttonColor = MaterialTheme.colorScheme.errorContainer,
                textColor = MaterialTheme.colorScheme.error,
                textStyle = MaterialTheme.typography.titleMedium,
                onClickAction = onclick
            )
        }

    }
}
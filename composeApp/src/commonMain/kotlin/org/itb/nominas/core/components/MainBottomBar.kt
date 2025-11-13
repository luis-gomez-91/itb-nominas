package org.itb.nominas.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import compose.icons.evaicons.outline.Home
import compose.icons.evaicons.outline.Person
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.Login
import compose.icons.tablericons.Logout
import compose.icons.tablericons.Qrcode
import org.itb.nominas.core.domain.BottomBarItem
import org.itb.nominas.core.navigation.HomeRoute
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.attendance.ui.NewEntry
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
    val bottomSheetNewEntry by mainViewModel.showBottomSheetNewEntry.collectAsState(false)
    val isHomeSelected = currentRoute == HomeRoute::class.qualifiedName
    val colaborador by mainViewModel.colaborador.collectAsState(null)
    val location by mainViewModel.location.collectAsState()
    val isSalida by remember(colaborador?.ultimoRegistro) {
        derivedStateOf { colaborador?.ultimoRegistro?.isSalida == true }
    }

    // Campo para ocultar el boton de marcar si no se le ha asignado aun un horario
    val showEntryButton by mainViewModel.showEntryButton.collectAsState()

    val navigationIcons = listOf<BottomBarItem>(
        BottomBarItem(
            onclick = { navController.navigate(HomeRoute) },
            label = "Inicio",
            icon = if (isHomeSelected) EvaIcons.Fill.Home else EvaIcons.Outline.Home,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall,
            isSelected = isHomeSelected
        ),

        BottomBarItem(
            onclick = { mainViewModel.setBottomSheetProfile(true) },
            label = "Perfil",
            icon = EvaIcons.Outline.Person,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall,
            isSelected = false
        ),

        BottomBarItem(
            onclick = { mainViewModel.setBottomSheetQR(true) },
            label = "QR Code",
            icon = TablerIcons.Qrcode,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall,
            isSelected = false
        )
    ).let { baseList ->
        if (showEntryButton) {
            baseList + BottomBarItem(
                onclick = {
                    mainViewModel.setShowBottomSheetNewEntry(true)
                },
                label = if (isSalida) "Marcar Salida" else "Marcar Ingreso",
                icon = if (isSalida) TablerIcons.Logout else TablerIcons.Login,
                color = if (isSalida) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.labelSmall,
                isSelected = false
            )
        } else {
            baseList
        }
    }

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
                    targetValue = 24.dp,
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
                            color = animatedColor,
                            textAlign = TextAlign.Center,
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
                photo = it.foto ?: "https://encrypted-tbn0.gstatic.com/im ages?q=tbn:ANd9GcQTz02QJMGkQbLyOApa3_ZDRqr_QiGJh120ZQ&s",
                onDismiss = {
                    mainViewModel.setBottomSheetQR(false)
                }
            )
        }

        if (bottomSheetNewEntry) {
            NewEntry(
                location = location,
                mainViewModel = mainViewModel,
                isSalida = it.ultimoRegistro?.isSalida == true,
                navHostController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetQR(
    qr: String,
    onDismiss: () -> Unit,
    photo: String
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
                text = "Identificador de colaborador",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                AsyncImage(
                    model = qr,
                    contentDescription = "QR Code",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                AsyncImage(
                    model = photo,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .offset(y = (-60).dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background),
                    contentScale = ContentScale.Crop
                )
            }
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
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            MyAssistChip(
                label = TextFormat("Última conexión:", if (colaborador.last_conection_date != null) "${colaborador.last_conection_date} (${colaborador.last_conection_time})" else "sin coincidencias").toString(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                labelColor = MaterialTheme.colorScheme.secondary,
                icon = TablerIcons.CalendarEvent
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
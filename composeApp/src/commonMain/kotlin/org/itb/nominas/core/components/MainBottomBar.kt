package org.itb.nominas.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Home
import compose.icons.evaicons.fill.LogOut
import compose.icons.evaicons.fill.Person
import compose.icons.evaicons.outline.Home
import compose.icons.evaicons.outline.Person
import org.itb.nominas.core.domain.BottomBarItem
import org.itb.nominas.core.navigation.HomeRoute
import org.itb.nominas.core.navigation.ProfileRoute
import org.itb.nominas.core.utils.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomBar(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomSheetProfile by mainViewModel.bottomSheetProfile.collectAsState(false)
    val isHomeSelected = currentRoute == HomeRoute::class.qualifiedName
    val isProfileSelected = currentRoute == ProfileRoute::class.qualifiedName
    val colaborador by mainViewModel.colaborador.collectAsState(null)

    val navigationIcons = listOf<BottomBarItem>(
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
        if (bottomSheetProfile) {
            ModalBottomSheet(
                onDismissRequest = { mainViewModel.setBottomSheetProfile(false) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AsyncImage(
                        model = it.foto,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Name
                    Text(
                        text = it.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = TextFormat("Última conexión:", "${it.last_conection_date} ${it.last_conection_time}"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = TextFormat("Usuario:", it.username),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = TextFormat("Sistema:", "${it.nombreSistema} (${it.urlSistema})"),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = TextFormat("Departamento:", it.area),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = TextFormat("Fecha de afiliación:", it.fechaAfiliacion),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = TextFormat("Décimo tercero:", it.decimo_tercero),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = TextFormat("Décimo cuarto:", it.decimo_cuarto),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = TextFormat("Fondos de reserva:", it.fondo_reserva),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    MyFilledTonalButton(
                        text = "Cerrar sesión",
                        enabled = true,
                        icon = EvaIcons.Fill.LogOut,
                        iconSize = 24.dp,
                        buttonColor = MaterialTheme.colorScheme.errorContainer,
                        textColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.titleMedium,
                        onClickAction = {
                            mainViewModel.logout(navController)
                        }
                    )
                }

            }
        }
    }

}
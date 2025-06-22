package org.itb.nominas.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Sun
import compose.icons.evaicons.outline.ChevronRight
import compose.icons.evaicons.outline.LogOut
import compose.icons.evaicons.outline.Person
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import kotlinx.coroutines.launch
import org.itb.nominas.core.domain.MainDrawerItem
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.core.utils.Theme
import org.itb.nominas.core.utils.getTheme

@Composable
fun PermissionRequestEffect(
    permission: Permission,
    onResult: (Boolean) -> Unit
) {
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)

    LaunchedEffect(controller) {
        controller.providePermission(permission)
        onResult(controller.isPermissionGranted(permission))
    }
}

@Composable
fun MyDrawerContent(
    drawerState: DrawerState,
    navHostController: NavHostController,
    mainViewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val colaborador = mainViewModel.colaborador.collectAsState(null)
    val selectedTheme by mainViewModel.selectedTheme.collectAsState(null)
    val showBottomSheetTheme by mainViewModel.bottomSheetTheme.collectAsState(false)

    val items = buildList {
        add(MainDrawerItem("Perfil", EvaIcons.Outline.Person) { mainViewModel.setBottomSheetProfile(true) })
        add(MainDrawerItem("Tema", selectedTheme?.getTheme()?.icon ?: EvaIcons.Fill.Sun) { mainViewModel.setBottomSheetTheme(true) })
        add(MainDrawerItem("Cerrar sesión", EvaIcons.Outline.LogOut) { mainViewModel.logout(navHostController) })
    }

    PermissionRequestEffect(Permission.COARSE_LOCATION) {

    }

    ModalDrawerSheet(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceBright)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()),
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Nóminas ITB",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                colaborador.value?.let {

                    AsyncImage(
                        model = it.foto,
                        contentDescription = "Foto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = it.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Departamento de ${it.area}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

                items.forEach { item ->
                    Column {
                        NavigationDrawerItem(
                            label = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = item.label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Icon(
                                        imageVector = EvaIcons.Outline.ChevronRight,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                item.onclick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    }
                }
            }
        }
    }

    if (showBottomSheetTheme) {
        ThemeSettings(mainViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettings (
    mainViewModel: MainViewModel
) {
    val theme by mainViewModel.selectedTheme.collectAsState()
    val themes = listOf(Theme.Light, Theme.Dark, Theme.SystemDefault)

    ModalBottomSheet(
        onDismissRequest = { mainViewModel.setBottomSheetTheme(false) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Seleccionar Tema",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
            }

            items(themes) { appTheme ->
                val themeItem = appTheme.getTheme()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { mainViewModel.setTheme(appTheme) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Icon(
                            imageVector = themeItem.icon,
                            contentDescription = themeItem.text,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text =  themeItem.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    RadioButton(
                        selected = appTheme == theme,
                        onClick = { mainViewModel.setTheme(appTheme) }
                    )
                }
                HorizontalDivider()
            }
        }

    }
}

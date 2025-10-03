package org.itb.nominas.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.MyCard
import org.itb.nominas.core.components.MyErrorAlert
import org.itb.nominas.core.components.ShimmerLoadingAnimation
import org.itb.nominas.core.navigation.AttendanceRoute
import org.itb.nominas.core.navigation.DeductionsRoute
import org.itb.nominas.core.navigation.HistoryRoute
import org.itb.nominas.core.navigation.PayRollRoute
import org.itb.nominas.core.navigation.TrackerRoute
import org.itb.nominas.features.home.data.ModuloResponse
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = koinViewModel()
) {
    MainScaffold(
        navController = navHostController,
        mainViewModel = homeViewModel.mainViewModel,
        content = { Screen(homeViewModel, navHostController) }
    )
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun Screen(
    homeViewModel: HomeViewModel,
    navHostController: NavHostController
) {
    val data by homeViewModel.data.collectAsState(null)
    val error by homeViewModel.error.collectAsState(null)
    val mainError by homeViewModel.mainViewModel.error.collectAsState(null)
    val isLoading by homeViewModel.isLoading.collectAsState(false)
    val attendanceLoading by homeViewModel.mainViewModel.attendanceLoading.collectAsState(false)
    val searchQuery by homeViewModel.mainViewModel.searchQuery.collectAsState(null)

    LaunchedEffect(Unit) {
        homeViewModel.loadHome()
    }

    if (isLoading || attendanceLoading) {
        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ShimmerLoadingAnimation(3)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            data?.modulos?.let { modulos ->

                val modulosFilter = modulos.filter { modulo ->
                    searchQuery.isNullOrBlank() ||
                    modulo.nombre.contains(searchQuery?:"", ignoreCase = true)
                }

                LazyColumn (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items (modulosFilter) { modulo ->
                        ModuloItem(
                            modulo = modulo,
                            onClick = { rutaSeleccionada ->
                                when (rutaSeleccionada) {
                                    PayRollRoute.route -> navHostController.navigate(PayRollRoute)
                                    DeductionsRoute.route -> navHostController.navigate(DeductionsRoute)
                                    AttendanceRoute.route -> navHostController.navigate(AttendanceRoute)
                                    TrackerRoute.route -> navHostController.navigate(TrackerRoute)
                                    HistoryRoute.route -> navHostController.navigate(HistoryRoute)
                                    else -> {}
                                }
                            }
                        )
                    }
                }
            }
//            if (attendanceLoading) {
//                FullScreenLoading(attendanceLoading)
//            }
        }

    }

    error?.let {
        MyErrorAlert(
            titulo = "Ocurrio un error inesperado",
            mensaje = it.message,
            onDismiss = {
                homeViewModel.clearError()
                homeViewModel.clearData()
                homeViewModel.mainViewModel.logout(navHostController)
            },
            showAlert = true
        )
    }

    mainError?.let {
        MyErrorAlert(
            titulo = "Error",
            mensaje = it.message,
            onDismiss = {
                homeViewModel.mainViewModel.clearError()
            },
            showAlert = true
        )
    }
}

@Composable
fun ModuloItem (
    modulo: ModuloResponse,
    onClick: (String) -> Unit
) {
    MyCard(
        onClick = {
            onClick(modulo.url)
        }
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = modulo.imagen,
                contentDescription = modulo.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(64.dp)
                    .aspectRatio(1f)
                    .padding(8.dp)
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = modulo.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = modulo.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
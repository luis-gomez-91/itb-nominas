package org.itb.nominas.features.payroll.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import compose.icons.TablerIcons
import compose.icons.tablericons.Download
import kotlinx.serialization.json.JsonPrimitive
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.MyCard
import org.itb.nominas.core.components.MyErrorAlert
import org.itb.nominas.core.components.ShimmerLoadingAnimation
import org.itb.nominas.core.data.request.ReportRequest
import org.itb.nominas.features.payroll.data.PayRollYear
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun PayRollScreen(
    navHostController: NavHostController
) {
    val payRollViewModel: PayRollViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        payRollViewModel.loadPayRoll()
    }

    MainScaffold(
        navController = navHostController,
        mainViewModel = payRollViewModel.mainViewModel,
        content = { Screen(payRollViewModel) }
    )
}

@Composable
fun Screen(
    payRollViewModel: PayRollViewModel
) {
    val data: List<PayRollYear> by payRollViewModel.data.collectAsState(emptyList())
    var selectedTabIndex by remember { mutableStateOf(0) }
    val isLoading by payRollViewModel.isLoading.collectAsState(false)
    val reportError by payRollViewModel.mainViewModel.reportError.collectAsState(null)

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { data.size }
    )

    LaunchedEffect(data) {
        if (data.isNotEmpty()) {
            selectedTabIndex = 0
            pagerState.scrollToPage(0)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
    }

    if (isLoading) {
        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ShimmerLoadingAnimation(12)
        }
    } else {
        if (data.isNotEmpty()) {
            ScrollableTabRowPayRoll(
                data = data,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index ->
                    selectedTabIndex = index
                }
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
            ) { index ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                ) {
                    PayRollItem(data.getOrNull(index), payRollViewModel)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No hay datos disponibles",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    reportError?.let {
        MyErrorAlert(
            titulo = "Error",
            mensaje = it.message,
            onDismiss = {
                payRollViewModel.mainViewModel.clearReportError()
            },
            showAlert = true
        )
    }
}

@Composable
fun ScrollableTabRowPayRoll(
    data: List<PayRollYear>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        indicator = { tabPositions ->
            SecondaryIndicator(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        data.forEachIndexed { index, payRoll ->
            Tab(
                modifier = Modifier.fillMaxWidth(0.5f),
                selected = selectedTabIndex == index,
                onClick = {
                    onTabSelected(index)
                },
                text = {
                    Text(
                        text = payRoll.year.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
fun PayRollItem(
    payRoll: PayRollYear?,
    payRollViewModel: PayRollViewModel
) {
    Spacer(Modifier.height(16.dp))
    payRoll?.let {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(it.rolPago) { rol ->
                Column (
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = rol.nombre,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.width(16.dp))

                        rol.reportName?.let {
                            IconButton (
                                onClick = {
                                    val form = ReportRequest(
                                        name = it,
                                        params = mapOf(
                                            "rol_id" to JsonPrimitive(rol.idRol),
                                            "persona" to JsonPrimitive(rol.idPersona)
                                        )
                                    )
                                    payRollViewModel.mainViewModel.downloadReport(form)
                                }
                            ) {
                                Icon(
                                    imageVector = TablerIcons.Download,
                                    contentDescription = "Descargar Rol",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    MyCard {
                        Column {
                            Text(
                                text = "Créditos",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Column (
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                            ) {
                                DeatailItem("Salario mensual", "$${rol.sueldo}")
                                DeatailItem("Extras (Todo)", "$${rol.beneficiosextra}")
                                DeatailItem("Total Ingresos", "$${rol.total}")
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    MyCard {
                        Column {
                                Text(
                                    text = "Débitos",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.titleSmall
                                )

                            Spacer(Modifier.height(4.dp))

                            Column (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                rol.detailDescuentos.forEach { descuento ->
                                    val desc = if (descuento.adicional != null) "(${descuento.adicional}) ${descuento.descripcion}" else descuento.descripcion
                                    DeatailItem(desc, "$${descuento.valor}")
                                }
                                DeatailItem("Total Descuentos", "$${rol.totalDescuentos}")
                            }

                        }
                    }

                    MyCard {
                        Column {
                            Row (
                                modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Valor a recibir FDM",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.titleSmall
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = "$${rol.fdm}",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
            }
        }

    } ?: run {
        Text(text = "No hay datos disponibles", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun DeatailItem(
    title: String,
    value: String
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            modifier = Modifier.weight(1f, fill = false),
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(32.dp))
        Text(
            modifier = Modifier,
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End
        )
    }
}
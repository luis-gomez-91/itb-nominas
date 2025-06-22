package org.itb.nominas.features.deductions.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.MyCard
import org.itb.nominas.core.components.ShimmerLoadingAnimation
import org.itb.nominas.core.components.TextFormat
import org.itb.nominas.features.deductions.data.DeductionResponse
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun DeductionScreen (
    navHostController: NavHostController
) {
    val deductionViewModel: DeductionViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        deductionViewModel.loadDeductions()
    }

    MainScaffold(
        navController = navHostController,
        mainViewModel = deductionViewModel.mainViewModel,
        content = { Screen(deductionViewModel) }
    )
}

@Composable
fun Screen(
    deductionViewModel: DeductionViewModel
) {
    val data by deductionViewModel.data.collectAsState(emptyList())
    val isLoading by deductionViewModel.isLoading.collectAsState(false)

    if (isLoading) {
        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ShimmerLoadingAnimation(3, 120.dp)
        }
    } else {
        if (data.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(data) { deduction ->
                    DeductionItem(deduction)
                }
            }
        }
    }
}

@Composable
fun DeductionItem(
    deduction: DeductionResponse
) {
    MyCard {
        Column (
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TextDeduction("Tipo:", deduction.tipo)
            TextDeduction("Fecha:", deduction.fecha)
            TextDeduction("Motivo:", deduction.motivo)
            TextDeduction("Deuda:", "$${deduction.valor}")
            TextDeduction("Cuota:", deduction.numCuota)
        }

    }
}

@Composable
fun TextDeduction(
    title: String,
    text: String
) {
    Text(
        text = TextFormat(title, text),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall
    )
}
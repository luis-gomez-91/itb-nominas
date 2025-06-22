package org.itb.nominas.features.attendance.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import org.itb.nominas.core.components.MainScaffold
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun AttendanceScreen(
    navHostController: NavHostController,
    attendanceViewModel: AttendanceViewModel = koinViewModel()
) {

    LaunchedEffect(Unit) {
        attendanceViewModel.loadAttendance()
    }

    MainScaffold(
        navController = navHostController,
        mainViewModel = attendanceViewModel.mainViewModel,
        content = { Screen(attendanceViewModel) }
    )
}

@Composable
fun Screen(
    attendanceViewModel: AttendanceViewModel
) {
    val data by attendanceViewModel.data.collectAsState(null)
    val isLoading by attendanceViewModel.isLoading.collectAsState(false)
    val error by attendanceViewModel.error.collectAsState(null)


}
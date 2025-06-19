package org.itb.nominas.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.itb.nominas.core.utils.BiometryViewModel
import org.itb.nominas.features.deductions.ui.DeductionScreen
import org.itb.nominas.features.home.ui.HomeScreen
import org.itb.nominas.features.login.ui.LoginScreen
import org.itb.nominas.features.payroll.ui.PayRollScreen

@Composable
fun NavigationWrapper(
    biometryViewModel: BiometryViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> { LoginScreen(navController, biometryViewModel) }
        composable<Home> { HomeScreen(navController) }
        composable<PayRoll> { PayRollScreen(navController) }
        composable<Deductions> { DeductionScreen(navController) }
    }
}

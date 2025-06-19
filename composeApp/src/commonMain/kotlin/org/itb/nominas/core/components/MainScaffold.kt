package org.itb.nominas.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.itb.nominas.core.utils.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    content: @Composable () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MyDrawerContent(
                drawerState,
                navController,
                mainViewModel
            )
        },
        content = {
            Scaffold(
                topBar = { MainTopBar(mainViewModel, drawerState) },
                bottomBar = { MainBottomBar(navController, mainViewModel) }
            ) { innerPadding ->
                Surface(
                    Modifier
                        .padding(innerPadding)
                        .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 4.dp)
                    ) {
                        content()
                    }
                }
            }
       }
    )
}

package org.itb.nominas.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.PermissionsController


@Composable
fun PermissionRequestEffect(
    permission: Permission,
    permissionsController: PermissionsController,
    onResult: (Boolean) -> Unit
) {

//    BindEffect(permissionsController)
    LaunchedEffect(permissionsController) {
        permissionsController.providePermission(permission)
        onResult(permissionsController.isPermissionGranted(permission))
    }
}

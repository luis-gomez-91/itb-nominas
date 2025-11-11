package org.itb.nominas.core.network

import io.github.aakira.napier.Napier
import org.itb.nominas.core.utils.AppSettings

class KtorSessionManager(
    private val clientManager: HttpClientManager
) : SessionManager {

    override fun onLoginSuccess() {
        Napier.i("SessionManager: Login exitoso", tag = "KtorAuth")

        val tokens = AppSettings.getToken()
        if (tokens != null) {
            Napier.i("SessionManager: Tokens confirmados en AppSettings", tag = "KtorAuth")
            Napier.d("SessionManager: Access token: ${tokens.access.take(20)}...", tag = "KtorAuth")
            Napier.d("SessionManager: Refresh token: ${tokens.refresh.take(20)}...", tag = "KtorAuth")
        } else {
            Napier.e("SessionManager: ERROR - No se encontraron tokens después del login", tag = "KtorAuth")
        }
    }

    override fun onLogout() {
        AppSettings.clearToken()
        clientManager.recreateClient()
    }
}
package org.itb.nominas.core.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import org.itb.nominas.core.utils.AppSettings

class KtorSessionManager(private val httpClient: HttpClient) : SessionManager {

    override fun onLoginSuccess() {
        Napier.i("SessionManager: Login exitoso", tag = "KtorAuth")

        // Verificar que los tokens estén guardados correctamente
        val tokens = AppSettings.getToken()
        if (tokens != null) {
            Napier.i("SessionManager: Tokens confirmados en AppSettings", tag = "KtorAuth")
            Napier.d("SessionManager: Access token: ${tokens.access.take(20)}...", tag = "KtorAuth")
            Napier.d("SessionManager: Refresh token: ${tokens.refresh.take(20)}...", tag = "KtorAuth")
        } else {
            Napier.e("SessionManager: ERROR - No se encontraron tokens después del login", tag = "KtorAuth")
        }

        // El plugin Auth automáticamente cargará estos tokens en el próximo request
        // No es necesario reconfigurar nada manualmente
    }

    override fun onLogout() {
        Napier.i("SessionManager: Iniciando logout", tag = "KtorAuth")

        // Limpiar todos los datos de la sesión
        AppSettings.clearAll()

        // El plugin Auth automáticamente retornará null en loadTokens
        // porque AppSettings.getToken() ahora retorna null

        Napier.i("SessionManager: Logout completado - Sesión limpiada completamente", tag = "KtorAuth")
    }
}
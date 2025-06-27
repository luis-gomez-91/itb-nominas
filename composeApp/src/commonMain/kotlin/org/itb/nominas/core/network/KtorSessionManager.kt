package org.itb.nominas.core.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.auth.providers.BearerTokens // Necesario para el contexto de Auth
import io.ktor.client.plugins.pluginOrNull
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

import org.itb.nominas.core.utils.AppSettings
import org.itb.nominas.core.utils.URL_SERVER
import org.itb.nominas.features.login.data.TokenResponse

class KtorSessionManager(private val httpClient: HttpClient) : SessionManager {

    override fun onLoginSuccess() {
        Napier.i("KtorSessionManager: Notificando a Auth plugin sobre LOGIN.", tag = "KtorAuth")

        httpClient.pluginOrNull(Auth)?.let { auth ->
            // Reconfigura el proveedor de autenticación Bearer
            // Esto le dice a Ktor que debe obtener un nuevo token de `loadTokens`
            // la próxima vez que se necesite una autenticación.
            auth.bearer {
                // Configura un `loadTokens` que obtiene el token directamente desde AppSettings
                loadTokens {
                    val currentTokens = AppSettings.getToken()
                    Napier.i("KtorSessionManager: Forzando recarga de token en Ktor. Token cargado: ${currentTokens?.access}", tag = "KtorAuth")
                    currentTokens?.let { BearerTokens(it.access, it.refresh) }
                }
                // Si también tienes refreshTokens, asegúrate de que esté configurado aquí también.
                // Tu bloque refreshTokens original va aquí:
                refreshTokens {
                    val current = AppSettings.getToken()
                    Napier.i("KtorAuth: refreshTokens llamado durante reconfiguración. Refresh token actual: ${current?.refresh}", tag = "KtorAuth")
                    if (current == null) {
                        Napier.w("KtorAuth: No hay token actual en AppSettings para refrescar.", tag = "KtorAuth")
                        return@refreshTokens null
                    }
                    try {
                        val response = httpClient.post("${URL_SERVER}token/refresh/") { // Usa httpClient aquí
                            contentType(ContentType.Application.Json)
                            setBody(mapOf("refresh" to current.refresh))
                        }.body<TokenResponse>() // Necesitarás importar TokenResponse si no lo está

                        AppSettings.setToken(response)
                        BearerTokens(response.access, response.refresh)
                    } catch (e: Exception) {
                        Napier.e("KtorAuth: Excepción al refrescar token: ${e.message}", e, tag = "KtorAuth")
                        null
                    }
                }
            }
        } ?: Napier.e("KtorSessionManager: El plugin Auth no está instalado en HttpClient durante LOGIN.", tag = "KtorAuth")
    }

    override fun onLogout() {
        Napier.i("KtorSessionManager: Notificando a Auth plugin sobre LOGOUT.", tag = "KtorAuth")

        httpClient.pluginOrNull(Auth)?.let { auth ->
            // Reconfigura el proveedor de autenticación Bearer para que no tenga tokens.
            auth.bearer {
                loadTokens { null } // Fuerza que no haya tokens
                refreshTokens { null } // Asegúrate de que no intente refrescar
            }
        } ?: Napier.e("KtorSessionManager: El plugin Auth no está instalado en HttpClient durante LOGOUT.", tag = "KtorAuth")
    }
}
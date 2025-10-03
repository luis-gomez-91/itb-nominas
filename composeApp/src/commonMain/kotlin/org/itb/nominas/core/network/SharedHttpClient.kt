package org.itb.nominas.core.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.itb.nominas.core.utils.AppSettings
import org.itb.nominas.core.utils.URL_SERVER
import org.itb.nominas.features.login.data.AccessTokenResponse
import org.itb.nominas.features.login.data.TokenResponse

fun provideHttpClient(): HttpClient = HttpClient {
    install(Auth) {
        bearer {
            loadTokens {
                val tokens = AppSettings.getToken()
                Napier.d("Auth: Cargando tokens - ${if (tokens != null) "Tokens encontrados" else "Sin tokens"}", tag = "KtorAuth")
                tokens?.let {
                    BearerTokens(
                        accessToken = it.access,
                        refreshToken = it.refresh
                    )
                }
            }

            refreshTokens {
                val current = AppSettings.getToken()
                Napier.i("Auth: Iniciando refresh de token", tag = "KtorAuth")

                if (current == null) {
                    Napier.w("Auth: No hay token actual para refrescar", tag = "KtorAuth")
                    return@refreshTokens null
                }

                // Verificar si el response anterior fue 401 Unauthorized
                val responseStatus = response.status
                if (responseStatus != HttpStatusCode.Unauthorized) {
                    Napier.d("Auth: No es necesario refresh (status: $responseStatus)", tag = "KtorAuth")
                    return@refreshTokens null
                }

                try {
                    Napier.d("Auth: Enviando request de refresh al servidor", tag = "KtorAuth")

                    val refreshResponse = client.post("${URL_SERVER}token/refresh/") {
                        contentType(ContentType.Application.Json)
                        setBody(mapOf("refresh" to current.refresh))
                        markAsRefreshTokenRequest()
                    }

                    if (!refreshResponse.status.isSuccess()) {
                        Napier.e("Auth: Refresh falló con status ${refreshResponse.status.value}", tag = "KtorAuth")
                        // Token refresh falló - limpiar y forzar re-login
                        AppSettings.clearToken()
                        return@refreshTokens null
                    }

                    val accessTokenResponse = refreshResponse.body<AccessTokenResponse>()
                    val newTokens = TokenResponse(
                        access = accessTokenResponse.access,
                        refresh = current.refresh
                    )

                    AppSettings.setToken(newTokens)

                    Napier.i("Auth: Token refrescado exitosamente", tag = "KtorAuth")
                    Napier.d("Auth: Nuevo access token: ${newTokens.access.take(20)}...", tag = "KtorAuth")

                    BearerTokens(
                        accessToken = newTokens.access,
                        refreshToken = newTokens.refresh
                    )
                } catch (e: Exception) {
                    Napier.e("Auth: Error al refrescar token - ${e.message}", e, tag = "KtorAuth")
                    // Limpiar tokens si el refresh falla
                    AppSettings.clearToken()
                    null
                }
            }

            val publicEndpoints = listOf(
                "/api/auth/login/",
                "/api/token/refresh/",
            )

            sendWithoutRequest { request ->
                val path = request.url.encodedPath.lowercase()
                val isPublic = publicEndpoints.any { path.startsWith(it) }
                Napier.d("Auth: Request a $path → ${if (isPublic) "SIN token" else "CON token"}", tag = "KtorAuth")
                !isPublic
            }
        }
    }

    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 15_000
    }

    install(ResponseObserver) {
        onResponse { response ->
            val endpoint = response.call.request.url.encodedPath
            Napier.d("HTTP Response: ${response.status.value} - $endpoint", tag = "KtorHTTP")

            // Log de headers en caso de error
            if (response.status.value in 400..599) {
                Napier.w("HTTP Error Headers:", tag = "KtorHTTP")
                response.headers.forEach { key, values ->
                    Napier.w("  $key = $values", tag = "KtorHTTP")
                }
            }
        }
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Napier.d(message, tag = "KtorLogging")
            }
        }
        level = LogLevel.HEADERS
    }
}
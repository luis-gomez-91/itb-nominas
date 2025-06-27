package org.itb.nominas.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.itb.nominas.core.utils.AppSettings
import org.itb.nominas.core.utils.URL_SERVER
import org.itb.nominas.features.login.data.TokenResponse

fun provideHttpClient(): HttpClient = HttpClient {
    install(Auth) {
        bearer {
            loadTokens {
                AppSettings.getToken()?.let { BearerTokens(it.access, it.refresh) }
            }
            refreshTokens {
                val current = AppSettings.getToken()
                if (current == null) return@refreshTokens null

                try {
                    val response = client.post("${URL_SERVER}token/refresh/") {
                        contentType(ContentType.Application.Json)
                        setBody(mapOf("refresh" to current.refresh))
                    }.body<TokenResponse>()

                    AppSettings.setToken(response)
                    BearerTokens(response.access, response.refresh)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
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
        requestTimeoutMillis = 30_000     // 30 segundos
        connectTimeoutMillis = 15_000     // 15 segundos
        socketTimeoutMillis = 15_000      // 15 segundos
    }

    install(ResponseObserver) {
        onResponse { response ->
            println("Respuesta del servidor: ${response.status}")
            response.headers.forEach { key, values ->
                println("Header: $key = $values")
            }
        }
    }

//    install(Logging) {
//        logger = object : Logger {
//            override fun log(message: String) {
//                Napier.d(message, tag = "KtorLogging")
//            }
//        }
//    }
}

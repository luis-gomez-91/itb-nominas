package com.luisdev.marknotes.data.remote.service

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.itb.nominas.features.login.data.LoginRequest
import org.itb.nominas.core.data.response.BaseResponse
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.features.login.data.LoginResponse
import org.itb.nominas.core.utils.URL_SERVER

class LoginService(
    private val client: HttpClient
) {
    suspend fun fetchLogin(body: LoginRequest): BaseResponse<LoginResponse> {
        return try {
            Napier.i("Intentando login para: ${body.username}", tag = "LoginService")

            val response: HttpResponse = client.post("${URL_SERVER}auth/login/") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            Napier.i("Status code: ${response.status.value}", tag = "LoginService")

            when {
                response.status.isSuccess() -> {
                    // La respuesta directa de Django es: { access, refresh, user }
                    val loginResponse = response.body<LoginResponse>()
                    Napier.i("Login exitoso para: ${body.username}", tag = "LoginService")

                    BaseResponse(
                        status = "success",
                        data = loginResponse
                    )
                }
                else -> {
                    // Intentar parsear el error de Django
                    try {
                        val errorResponse = response.body<ErrorResponse>()
                        Napier.e("Error en login: ${errorResponse.message}", tag = "LoginService")
                        BaseResponse(
                            status = "error",
                            error = errorResponse
                        )
                    } catch (e: Exception) {
                        Napier.e("Error parseando respuesta de error", e, tag = "LoginService")
                        BaseResponse(
                            status = "error",
                            error = ErrorResponse(
                                code = "http_${response.status.value}",
                                message = "Error en la autenticación: ${response.status.description}"
                            )
                        )
                    }
                }
            }

        } catch (e: Exception) {
            Napier.e("Excepción durante login: ${e.message}", e, tag = "LoginService")
            BaseResponse(
                status = "error",
                error = ErrorResponse(
                    code = "network_error",
                    message = e.message ?: "Error de conexión"
                )
            )
        }
    }
}
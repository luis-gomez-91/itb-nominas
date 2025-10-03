package org.itb.nominas.core.data.service

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import org.itb.nominas.core.data.response.BaseResponse
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.utils.URL_SERVER
import org.itb.nominas.features.home.data.HomeResponse

class HomeService(
    private val client: HttpClient
) {

    suspend fun fetchHome(): BaseResponse<HomeResponse> {
        return try {
            Napier.d("Iniciando request a /api/home/", tag = "HomeService")

            val response = client.get("${URL_SERVER}home/") {
                contentType(ContentType.Application.Json)
            }

            // Solo log en desarrollo
            val rawJson = response.bodyAsText()
            Napier.d("Respuesta exitosa de /api/home/", tag = "HomeService")
            Napier.i("Respuesta cruda del servidor: $rawJson", tag = "HomeService")


            // Deserializar la respuesta
            response.body<BaseResponse<HomeResponse>>()

        } catch (e: ClientRequestException) {
            // Error 4xx (cliente)
            Napier.e("Error del cliente ${e.response.status}: ${e.message}", tag = "HomeService")

            when (e.response.status) {
                HttpStatusCode.Unauthorized -> {
                    // El token expiró o es inválido
                    BaseResponse(
                        status = "error",
                        error = ErrorResponse(
                            code = "unauthorized",
                            message = "Sesión expirada. Por favor, inicia sesión nuevamente."
                        )
                    )
                }
                HttpStatusCode.Forbidden -> {
                    BaseResponse(
                        status = "error",
                        error = ErrorResponse(
                            code = "forbidden",
                            message = "No tienes permisos para acceder a este recurso."
                        )
                    )
                }
                else -> {
                    BaseResponse(
                        status = "error",
                        error = ErrorResponse(
                            code = "client_error_${e.response.status.value}",
                            message = "Error al obtener datos: ${e.response.status.description}"
                        )
                    )
                }
            }
        } catch (e: ServerResponseException) {
            // Error 5xx (servidor)
            Napier.e("Error del servidor ${e.response.status}: ${e.message}", tag = "HomeService")
            BaseResponse(
                status = "error",
                error = ErrorResponse(
                    code = "server_error_${e.response.status.value}",
                    message = "Error en el servidor. Intenta más tarde."
                )
            )
        } catch (e: CancellationException) {
            // La coroutine fue cancelada - relanzar
            throw e
        } catch (e: Exception) {
            // Cualquier otro error
            Napier.e("Error inesperado: ${e.message}", e, tag = "HomeService")
            BaseResponse(
                status = "error",
                error = ErrorResponse(
                    code = "unknown_error",
                    message = "Error inesperado: ${e.message ?: "Sin mensaje"}"
                )
            )
        }
    }
}
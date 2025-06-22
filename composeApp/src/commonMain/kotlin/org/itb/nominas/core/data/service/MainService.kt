package org.itb.nominas.core.data.service

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.itb.nominas.core.data.request.RefreshTokenRequest
import org.itb.nominas.core.data.request.ReportRequest
import org.itb.nominas.core.data.response.BaseResponse
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.response.MessageResponse
import org.itb.nominas.core.utils.URL_SERVER

class MainService (
    private val client: HttpClient
) {
    suspend fun fetchLogout(
        refreshToken: RefreshTokenRequest
    ): BaseResponse<MessageResponse> {
        return try {
            val response = client.post("${URL_SERVER}auth/logout/") {
                contentType(ContentType.Application.Json)
                setBody(refreshToken)
            }
            response.body<BaseResponse<MessageResponse>>()

        } catch (e: Exception) {
            BaseResponse(
                status = "error",
                error = ErrorResponse(code = "error", message = "$e")
            )
        }
    }

    suspend fun downloadReport(
        body: ReportRequest
    ): BaseResponse<MessageResponse> {
        return try {
            val response = client.post("${URL_SERVER}run_report/") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            val bodyAsText = response.bodyAsText()
            Napier.e("Respuesta cruda: $bodyAsText" , tag = "prueba")
            Napier.e("Código de estado: ${response.status}" , tag = "prueba")
            response.body<BaseResponse<MessageResponse>>()

        } catch (e: Exception) {
            BaseResponse(
                status = "error",
                error = ErrorResponse(code = "error", message = "$e")
            )
        }
    }
}
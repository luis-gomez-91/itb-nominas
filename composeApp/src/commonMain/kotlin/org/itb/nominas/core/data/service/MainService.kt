package org.itb.nominas.core.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.itb.nominas.core.data.request.RefreshTokenRequest
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
}
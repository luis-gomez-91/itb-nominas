package com.luisdev.marknotes.data.remote.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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
            val response = client.post("${URL_SERVER}auth/login/") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            response.body<BaseResponse<LoginResponse>>()

        } catch (e: Exception) {
            BaseResponse(
                status = "error",
                error = ErrorResponse(code = "error", message = "$e")
            )
        }
    }

}



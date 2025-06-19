package org.itb.nominas.core.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.itb.nominas.core.data.response.BaseResponse
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.utils.URL_SERVER
import org.itb.nominas.features.home.data.HomeResponse

class HomeService(
    private val client: HttpClient
) {
    suspend fun fetchHome(): BaseResponse<HomeResponse> {
        return try {
            val response = client.get("${URL_SERVER}home/") {
//                headers {
//                    append(HttpHeaders.Authorization, "Bearer $accessToken")
//                }
                contentType(ContentType.Application.Json)
            }
            response.body<BaseResponse<HomeResponse>>()

        } catch (e: Exception) {
            BaseResponse(
                status = "error",
                error = ErrorResponse(code = "exception", message = "$e")
            )
        }
    }

}
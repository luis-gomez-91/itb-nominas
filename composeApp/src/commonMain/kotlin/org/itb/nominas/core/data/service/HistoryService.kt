package org.itb.nominas.core.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.itb.nominas.core.data.response.BaseResponse
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.network.HttpClientManager
import org.itb.nominas.core.utils.URL_SERVER
import org.itb.nominas.features.history.data.HistoryResponse

class HistoryService(
    private val clientManager: HttpClientManager
) {
    suspend fun fetchHistory(endPoint: String, page: Int,): BaseResponse<HistoryResponse> {
        return try {
            val response = clientManager.getClient().get("${URL_SERVER}$endPoint/history/$page/") {
                contentType(ContentType.Application.Json)
            }
            response.body<BaseResponse<HistoryResponse>>()

        } catch (e: Exception) {
            BaseResponse(
                status = "error",
                error = ErrorResponse(code = "exception", message = "$e")
            )
        }
    }
}
package org.itb.nominas.core.data.service

import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.itb.nominas.core.data.response.BaseResponse
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.response.MessageResponse
import org.itb.nominas.core.network.HttpClientManager
import org.itb.nominas.core.utils.URL_SERVER
import org.itb.nominas.features.tracker.data.TrackerRequest
import org.itb.nominas.features.tracker.data.TrackerResponse


class TrackerService(
    private val clientManager: HttpClientManager
) {
    suspend fun fetchTracker(
        endPoint: String,
        page: Int,
        searchQuery: String? = null
    ): BaseResponse<TrackerResponse> {
        return try {
            val response = clientManager.getClient().get("${URL_SERVER}$endPoint/$page") {
                contentType(ContentType.Application.Json)
                url {
                    searchQuery?.takeIf { it.isNotBlank() }?.let {
                        parameters.append("search", it)
                    }
                }
            }
            response.body<BaseResponse<TrackerResponse>>()

        } catch (e: Exception) {
            BaseResponse(
                status = "error",
                error = ErrorResponse(code = "exception", message = "$e")
            )
        }
    }

    suspend fun newTracker(endPoint: String, body: TrackerRequest): BaseResponse<MessageResponse> {
        return try {
            val response = clientManager.getClient().post("${URL_SERVER}$endPoint/create/") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            val parsedResponse = response.body<BaseResponse<MessageResponse>>()
            Napier.i("RERSPONSE: $parsedResponse", tag = "Bitacora")
            return parsedResponse

        } catch (e: Exception) {
            Napier.e("ERROR: $e", tag = "Bitacora")
            BaseResponse(
                status = "error",
                error = ErrorResponse(code = "exception", message = "$e")
            )
        }
    }

}
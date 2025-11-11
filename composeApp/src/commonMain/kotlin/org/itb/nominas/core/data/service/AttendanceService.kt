package org.itb.nominas.core.data.service

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
import org.itb.nominas.features.attendance.data.request.AttendanceEntryRequest
import org.itb.nominas.features.attendance.data.response.AttendanceResponse


class AttendanceService(
    private val clientManager: HttpClientManager
) {
    suspend fun fetchAttendance(endPoint: String): BaseResponse<AttendanceResponse> {
        return try {
            val response = clientManager.getClient().get("${URL_SERVER}$endPoint/today/") {
                contentType(ContentType.Application.Json)
            }
            response.body<BaseResponse<AttendanceResponse>>()

        } catch (e: Exception) {
            BaseResponse(
                status = "error",
                error = ErrorResponse(code = "exception", message = "$e")
            )
        }
    }

    suspend fun newEntry(body: AttendanceEntryRequest): BaseResponse<MessageResponse> {
        return try {
            val response = clientManager.getClient().post("${URL_SERVER}registroasistencia/check-in/") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            response.body<BaseResponse<MessageResponse>>()

        } catch (e: Exception) {
            BaseResponse(
                status = "error",
                error = ErrorResponse(code = "exception", message = "$e")
            )
        }
    }

}
package org.itb.nominas.core.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.itb.nominas.core.data.response.BaseResponse
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.utils.URL_SERVER
import org.itb.nominas.features.attendance.data.AttendanceResponse


class AttendanceService(
    private val client: HttpClient
) {
    suspend fun fetchAttendance(endPoint: String): BaseResponse<AttendanceResponse> {
        return try {
            val response = client.get("${URL_SERVER}$endPoint/today/") {
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

}
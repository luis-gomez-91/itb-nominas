package org.itb.nominas.features.history.data

import kotlinx.serialization.Serializable
import org.itb.nominas.core.data.response.PagingResponse

@Serializable
data class HistoryResponse (
    val paging: PagingResponse,
    val historialAsistencias: List<HistoryAttendanceResponse>
)
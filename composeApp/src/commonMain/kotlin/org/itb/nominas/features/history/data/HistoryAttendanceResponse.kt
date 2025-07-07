package org.itb.nominas.features.history.data
import kotlinx.serialization.Serializable

@Serializable
data class HistoryAttendanceResponse(
    val detalles: List<HistoryDetailResponse>,
    val fecha: String,
    val horas: String,
    val first: HistoryMainDetailResponse?,
    val last: HistoryMainDetailResponse?
)

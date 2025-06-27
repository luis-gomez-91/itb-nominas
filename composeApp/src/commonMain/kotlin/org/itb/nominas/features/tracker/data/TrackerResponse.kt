package org.itb.nominas.features.tracker.data

import kotlinx.serialization.Serializable
import org.itb.nominas.core.data.response.PagingResponse

@Serializable
data class TrackerResponse(
    val bitacoras: List<TrackerItemResponse>,
    val paging: PagingResponse
)
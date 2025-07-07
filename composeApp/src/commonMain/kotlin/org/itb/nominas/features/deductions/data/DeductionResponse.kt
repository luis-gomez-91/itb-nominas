package org.itb.nominas.features.deductions.data

import kotlinx.serialization.Serializable
import org.itb.nominas.core.data.response.PagingResponse


@Serializable
data class DeductionResponse(
    val paging: PagingResponse,
    val descuentos: List<DeductionItemResponse>
)
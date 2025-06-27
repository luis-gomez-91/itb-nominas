package org.itb.nominas.core.data.response

import kotlinx.serialization.Serializable

@Serializable
data class PagingResponse(
    val firstPage: Int,
    val lastPage: Int
)
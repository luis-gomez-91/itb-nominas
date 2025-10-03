package org.itb.nominas.core.data.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val status: String = "success",
    val data: T? = null,
    val error: ErrorResponse? = null
)
package org.itb.nominas.core.data.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ReportRequest(
    val name: String,
    val params: Map<String, JsonElement>
)
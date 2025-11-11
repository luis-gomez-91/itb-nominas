package org.itb.nominas.core.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient


class HttpClientManager {
    private var _client: HttpClient? = null

    fun getClient(): HttpClient {
        if (_client == null) {
            _client = provideHttpClient()
            Napier.i("HttpClientManager: Nuevo cliente HTTP creado", tag = "HttpClient")
        }
        return _client!!
    }

    fun recreateClient() {
        Napier.i("HttpClientManager: Recreando cliente HTTP (cierre y nueva instancia)", tag = "HttpClient")
        _client?.close()
        _client = null
        _client = provideHttpClient()
    }

    fun close() {
        Napier.i("HttpClientManager: Cerrando cliente HTTP", tag = "HttpClient")
        _client?.close()
        _client = null
    }
}
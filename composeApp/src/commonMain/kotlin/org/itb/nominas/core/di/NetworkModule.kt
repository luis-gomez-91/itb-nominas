package org.itb.nominas.core.di

import org.itb.nominas.core.network.HttpClientManager
import org.itb.nominas.core.network.KtorSessionManager
import org.itb.nominas.core.network.SessionManager
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientManager() }
    single { get<HttpClientManager>().getClient() }
    single<SessionManager> {
        KtorSessionManager(
            clientManager = get()
        )
    }
}
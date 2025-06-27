package org.itb.nominas.core.di

import org.itb.nominas.core.network.KtorSessionManager
import org.itb.nominas.core.network.SessionManager
import org.itb.nominas.core.network.provideHttpClient
import org.koin.dsl.module

val networkModule = module {
    single { provideHttpClient() }
    single<SessionManager> { KtorSessionManager(get()) }
}
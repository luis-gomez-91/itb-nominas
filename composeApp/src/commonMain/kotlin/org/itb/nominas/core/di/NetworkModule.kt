package org.itb.nominas.core.di

import org.itb.nominas.core.network.provideHttpClient
import org.koin.dsl.module

val networkModule = module {
    single { provideHttpClient() }
}
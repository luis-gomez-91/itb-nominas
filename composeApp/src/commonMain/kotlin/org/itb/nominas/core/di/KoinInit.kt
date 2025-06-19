package org.itb.nominas.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

expect val nativeModule: Module

fun initKoin(
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(
            networkModule,
            serviceModule,
            viewModelModule,
            nativeModule
        )
    }
}
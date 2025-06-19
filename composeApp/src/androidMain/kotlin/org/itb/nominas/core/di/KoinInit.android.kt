package org.itb.nominas.core.di

import org.itb.nominas.core.platform.URLOpener
import org.itb.nominas.core.platform.URLOpenerAndroid
import org.koin.dsl.module


actual val nativeModule = module {
    single<URLOpener> { URLOpenerAndroid(get()) }
}
package org.itb.nominas.core.di

import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.ios.PermissionsController
import org.itb.nominas.core.platform.URLOpener
import org.itb.nominas.core.platform.URLOpenerIOS
import org.koin.dsl.module

actual val nativeModule = module {
    single<URLOpener> { URLOpenerIOS() }
    single { PermissionsController() }
    single { LocationTracker(get()) }

}
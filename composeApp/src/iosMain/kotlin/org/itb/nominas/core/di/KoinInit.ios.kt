package org.itb.nominas.core.di

import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.ios.PermissionsController
import org.itb.nominas.core.platform.IOSLocationService
import org.itb.nominas.core.platform.LocationService
import org.itb.nominas.core.platform.URLOpener
import org.itb.nominas.core.platform.URLOpenerIOS
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val nativeModule = module {
    single<URLOpener> { URLOpenerIOS() }
    single { PermissionsController() }
    single { LocationTracker(get()) }

//    single<LocationService> { IOSLocationService() }
    factoryOf(::IOSLocationService) bind LocationService::class

}
package org.itb.nominas.core.di

import dev.icerock.moko.permissions.ios.PermissionsController
import org.itb.nominas.core.platform.IOSLocationService
import org.itb.nominas.core.platform.LocationService
import org.itb.nominas.core.platform.SettingsOpener
import org.itb.nominas.core.platform.SettingsOpenerIos
import org.itb.nominas.core.platform.URLOpener
import org.itb.nominas.core.platform.URLOpenerIOS
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val nativeModule = module {
    single<URLOpener> { URLOpenerIOS() }
    single { PermissionsController() }
//    single<LocationService> { IOSLocationService() }
    factoryOf(::IOSLocationService) bind LocationService::class
    single<SettingsOpener> { SettingsOpenerIos() }

}
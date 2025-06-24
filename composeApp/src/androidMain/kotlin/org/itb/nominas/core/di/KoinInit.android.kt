package org.itb.nominas.core.di

import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import org.itb.nominas.core.platform.AndroidLocationService
import org.itb.nominas.core.platform.LocationService
import org.itb.nominas.core.platform.URLOpener
import org.itb.nominas.core.platform.URLOpenerAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


actual val nativeModule = module {
    single<URLOpener> { URLOpenerAndroid(get()) }
    single { PermissionsController(applicationContext = androidContext()) }
//    single { PermissionsController(applicationContext = get()) }
    single { LocationTracker(get()) }

    factory { Geocoder(get()) }
    factory { LocationServices.getFusedLocationProviderClient(androidContext()) }
    factoryOf(::AndroidLocationService) bind LocationService::class
}
package org.itb.nominas.core.di

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import dev.icerock.moko.permissions.PermissionsController
import org.itb.nominas.core.platform.AndroidLocationService
import org.itb.nominas.core.platform.LocationService
import org.itb.nominas.core.platform.SettingsOpener
import org.itb.nominas.core.platform.SettingsOpenerAndroid
import org.itb.nominas.core.platform.URLOpener
import org.itb.nominas.core.platform.URLOpenerAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


actual val nativeModule = module {
    single<URLOpener> { URLOpenerAndroid(get()) }
    single<SettingsOpener> { SettingsOpenerAndroid(get()) }
    single { PermissionsController(applicationContext = androidContext()) }
    factory { Geocoder(get()) }
    factory { LocationServices.getFusedLocationProviderClient(androidContext()) }
    factory { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    factoryOf(::AndroidLocationService) bind LocationService::class
}
package org.itb.nominas.core.platform

import android.content.Context
import android.location.LocationManager
import org.itb.nominas.MyApplication

actual fun isLocationEnabled(): Boolean {
    val context = MyApplication.context
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}
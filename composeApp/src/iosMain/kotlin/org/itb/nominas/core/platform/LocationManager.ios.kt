package org.itb.nominas.core.platform

import platform.CoreLocation.CLLocationManager

actual fun isLocationEnabled(): Boolean {
    return CLLocationManager.locationServicesEnabled()
}
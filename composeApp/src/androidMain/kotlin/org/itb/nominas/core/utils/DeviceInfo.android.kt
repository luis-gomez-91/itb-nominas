package org.itb.nominas.core.utils

import android.os.Build

actual class DeviceInfo actual constructor() {
    actual fun getUserAgent(): String {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL.replace(manufacturer, "", ignoreCase = true).trim()
        val deviceName = if (model.isNotEmpty()) "$manufacturer $model" else manufacturer

        return "$deviceName / Android ${Build.VERSION.RELEASE} / AppMobile"
    }
}
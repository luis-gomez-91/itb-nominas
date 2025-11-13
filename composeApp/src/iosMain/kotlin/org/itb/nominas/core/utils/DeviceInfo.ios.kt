package org.itb.nominas.core.utils

import platform.UIKit.UIDevice

actual class DeviceInfo actual constructor() {
    actual fun getUserAgent(): String {
        return "${UIDevice.currentDevice.model} / iOS ${UIDevice.currentDevice.systemVersion} / AppMobile"
    }
}
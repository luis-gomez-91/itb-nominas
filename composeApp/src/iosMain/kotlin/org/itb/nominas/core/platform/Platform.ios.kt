package org.itb.nominas.core.platform


class IOSPlatform: Platform {
//    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val name: String = "IOS"
}

actual fun getPlatform(): Platform = IOSPlatform()
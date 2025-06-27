package org.itb.nominas.core.platform

import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun appIsLastVersion(lastVersion: Int): Boolean {
    // Obtén la versión actual de la app en iOS
    val currentVersion = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "0"
    val latestVersion = "1.0" // Obtén la última versión desde un servidor o API

    return currentVersion >= latestVersion
}

actual fun openPlayStoreOrAppStore() {
    // En iOS, redirigimos a la App Store
    val appStoreUrl = "itms-apps://itunes.apple.com/app/idAPP_ID"
    UIApplication.sharedApplication.openURL(NSURL(string = appStoreUrl))
}
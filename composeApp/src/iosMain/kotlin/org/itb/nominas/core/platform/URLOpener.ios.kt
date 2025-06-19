package org.itb.nominas.core.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class URLOpenerIOS : URLOpener {
    override fun openURL(url: String) {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl != null) {
            UIApplication.sharedApplication.openURL(
                nsUrl,
                options = emptyMap<Any?, Any>(),
                completionHandler = { success ->
//                    println("URL opened: $success")
                }
            )
        }
    }
}
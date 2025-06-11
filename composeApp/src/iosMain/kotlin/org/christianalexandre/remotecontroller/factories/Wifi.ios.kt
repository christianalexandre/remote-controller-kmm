package org.christianalexandre.remotecontroller.factories

import platform.UIKit.*
import platform.Foundation.*

actual fun getWifiSSID(): String? {
    return null
}

actual fun goToWifiSettings() {
    val url = NSURL.URLWithString("App-Prefs:root=WIFI") ?: return

    if (UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(
            url,
            options = emptyMap<Any?, Any?>(),
            completionHandler = null
        )
    }
}
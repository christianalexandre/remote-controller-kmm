package org.christianalexandre.remotecontroller.factories

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import org.christianalexandre.remotecontroller.appContext

actual fun getWifiSSID(): String? {
    val context = appContext

    val hasFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasWifiState = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_WIFI_STATE
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFineLocation || !hasWifiState) {
        return null
    }

    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val ssid = wifiManager.connectionInfo.ssid
    return ssid?.removePrefix("\"")?.removeSuffix("\"")
}

actual fun goToWifiSettings() {
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    appContext.startActivity(intent)
}

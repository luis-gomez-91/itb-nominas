package org.itb.nominas.core.platform

import android.content.Context
import android.content.Intent
import android.provider.Settings


class SettingsOpenerAndroid(private val context: Context) : SettingsOpener {
    override fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
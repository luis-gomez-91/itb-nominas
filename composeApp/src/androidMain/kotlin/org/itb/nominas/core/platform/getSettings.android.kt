// androidMain/platform/Settings.kt
package org.itb.nominas.core.platform

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

// Variable para almacenar el contexto de la aplicación, inicializada una vez
private lateinit var applicationCtx: Context // Renombrada para mayor claridad

fun initSettingsContext(ctx: Context) {
    // Almacenamos el contexto de la aplicación, que es de larga duración
    applicationCtx = ctx.applicationContext
}

// Instancia singleton de Settings, creada una única vez de forma lazy
private val appSettingsInstance: Settings by lazy {
    // Usamos el applicationCtx para obtener las SharedPreferences
    val delegate = applicationCtx.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
    SharedPreferencesSettings(delegate)
}

actual fun getSettings(): Settings {
    // Siempre retornamos la misma instancia singleton
    return appSettingsInstance
}
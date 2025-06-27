// org.itb.nominas.core.utils.AppSettings.kt
package org.itb.nominas.core.utils

import org.itb.nominas.core.platform.getSettings
import org.itb.nominas.features.login.data.TokenResponse


object AppSettings {
    private val settings by lazy {
        getSettings()
    }

    fun setUsername(user: String) {
        settings.putString(KEY_USERNAME, user)
    }

    fun getUsername(): String? {
        val username = settings.getStringOrNull(KEY_USERNAME)
        return username
    }

    fun setPassword(pass: String) {
        // ¡ADVERTENCIA! Almacenar contraseñas en SharedPreferences no es seguro.
        // Considera usar un almacenamiento seguro (KeyStore/Keychain) si es una contraseña real.
        settings.putString(KEY_PASSWORD, pass)
    }

    fun getPassword(): String? {
        val password = settings.getStringOrNull(KEY_PASSWORD)
        return password
    }

    fun setTheme(theme: Theme) {
        settings.putString(KEY_THEME, theme.name)
    }

    fun getTheme(): Theme {
        val value = settings.getString(KEY_THEME, defaultValue = Theme.SystemDefault.name)
        val theme = Theme.entries.firstOrNull { it.name == value } ?: Theme.SystemDefault
        return theme
    }

    fun setToken(token: TokenResponse) {
        settings.putString(KEY_ACCESS_TOKEN, token.access)
        settings.putString(KEY_REFRESH_TOKEN, token.refresh)
    }

    fun clearToken() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    fun getToken(): TokenResponse? {
        val access = settings.getStringOrNull(KEY_ACCESS_TOKEN)
        val refresh = settings.getStringOrNull(KEY_REFRESH_TOKEN)

        if (access != null && refresh != null) {
            return TokenResponse(access = access, refresh = refresh)
        } else {
            return null
        }
    }
}
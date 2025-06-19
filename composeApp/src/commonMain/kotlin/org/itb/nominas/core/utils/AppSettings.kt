package org.itb.nominas.core.utils

import org.itb.nominas.core.platform.getSettings
import org.itb.nominas.features.login.data.TokenResponse

object AppSettings {
    private val settings by lazy { getSettings() }

    fun setUsername(user: String) {
        settings.putString(USERNAME, user)
    }

    fun getUsername(): String? {
        return settings.getStringOrNull(USERNAME)
    }

    fun setPassword(pass: String) {
        settings.putString(PASSWORD, pass)
    }

    fun getPassword(): String? {
        return settings.getStringOrNull(PASSWORD)
    }

    fun setTheme(theme: Theme) {
        settings.putString(KEY_THEME, theme.name)
    }

    fun getTheme(): Theme {
        val value = settings.getString(KEY_THEME, defaultValue = Theme.SystemDefault.name)
        return Theme.entries.firstOrNull { it.name == value } ?: Theme.SystemDefault
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

        return if (!access.isNullOrBlank() && !refresh.isNullOrBlank()) {
            TokenResponse(access = access, refresh = refresh)
        } else {
            null
        }
    }
}
package org.itb.nominas.core.network

interface SessionManager {
    fun onLoginSuccess()
    fun onLogout()
}
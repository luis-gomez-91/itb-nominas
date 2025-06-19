package org.itb.nominas.core.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
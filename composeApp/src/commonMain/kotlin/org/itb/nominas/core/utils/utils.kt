package org.itb.nominas.core.utils

/**
 * Formatea un Double con el número especificado de decimales
 */
fun Double.toCoordinateString(): String {
    val str = this.toString()
    val parts = str.split('.')
    if (parts.size == 1) return "$str.000000"

    val decimal = parts[1].take(6).padEnd(6, '0')
    return "${parts[0]}.$decimal"
}
package org.itb.nominas.core.platform

import org.itb.nominas.core.domain.LocationItem


interface LocationService {
    suspend fun fetchLocation(): LocationItem?
}
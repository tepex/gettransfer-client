package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.LocationEntity
import org.koin.standalone.KoinComponent

interface GeoRemote : KoinComponent {
    suspend fun getMyLocation(): LocationEntity
}
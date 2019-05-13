package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Location
import com.kg.gettransfer.domain.model.Result

interface GeoRemoteRepository {
    suspend fun getMyLocationByIp(): Result<Location>
}
package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.domain.repository.RouteRepository

class RouteRepositoryImpl(private val remote: RouteRemote): RouteRepository {
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean) =
        remote.getRouteInfo(from, to, withPrices, returnWay)
}

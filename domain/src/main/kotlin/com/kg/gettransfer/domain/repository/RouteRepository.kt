package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.RouteInfo

interface RouteRepository {
    suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean): RouteInfo
}

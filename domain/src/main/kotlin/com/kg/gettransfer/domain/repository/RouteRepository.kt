package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.RouteInfo

interface RouteRepository {
    suspend fun getRouteInfo(from: Point, to: Point, withPrices: Boolean, returnWay: Boolean): RouteInfo?
}

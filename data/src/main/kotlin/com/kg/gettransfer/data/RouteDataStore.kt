package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity

interface RouteDataStore {
    suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean): RouteInfoEntity
}

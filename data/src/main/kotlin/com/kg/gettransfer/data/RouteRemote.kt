package com.kg.gettransfer.data

interface RouteRemote {
    suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean): RouteInfo
}

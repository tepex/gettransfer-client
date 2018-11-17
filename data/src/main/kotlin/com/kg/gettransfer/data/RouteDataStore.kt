package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity

import org.koin.standalone.KoinComponent

interface RouteDataStore: KoinComponent {
    suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean): RouteInfoEntity
}

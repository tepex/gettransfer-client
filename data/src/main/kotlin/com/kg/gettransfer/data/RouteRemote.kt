package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity

import org.koin.standalone.KoinComponent

interface RouteRemote: KoinComponent {
    suspend fun getRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean, currency: String, dateTime: String?): RouteInfoEntity
    suspend fun getRouteInfo(points: Array<String>, hourlyDuration: Int, currency: String, dateTime: String?): RouteInfoEntity
}

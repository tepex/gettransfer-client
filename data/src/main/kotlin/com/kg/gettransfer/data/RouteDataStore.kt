package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity

import org.koin.standalone.KoinComponent
import java.util.Date

interface RouteDataStore: KoinComponent {
    suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean? = null, returnWay: Boolean? = null, currency: String? = null, dateTime: Date?): RouteInfoEntity?
    suspend fun getRouteInfo(from: String, hourlyDuration: Int, currency: String, dateTime: Date?): RouteInfoEntity?
    suspend fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity)
}

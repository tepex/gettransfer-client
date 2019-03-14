package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity

import org.koin.standalone.KoinComponent

interface RouteCache: KoinComponent {
    fun getRouteInfo(from: String, to: String): RouteInfoEntity?
    fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity)
}

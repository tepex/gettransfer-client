package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity
import org.koin.standalone.KoinComponent

interface RouteCache : KoinComponent {

    suspend fun getRouteInfo(from: String, to: String): RouteInfoEntity?

    suspend fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity)
}

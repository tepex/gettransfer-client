package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.RouteInfoRequestEntity
import org.koin.core.KoinComponent

interface RouteDataStore : KoinComponent {

    suspend fun getRouteInfo(request: RouteInfoRequestEntity): RouteInfoEntity?

    suspend fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity)
}

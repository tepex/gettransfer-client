package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.RouteInfoRequestEntity
import com.kg.gettransfer.data.model.RouteInfoHourlyRequestEntity
import org.koin.standalone.KoinComponent

interface RouteDataStore : KoinComponent {

    suspend fun getRouteInfo(request: RouteInfoRequestEntity): RouteInfoEntity?

    suspend fun getRouteInfo(request: RouteInfoHourlyRequestEntity): RouteInfoEntity?

    suspend fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity)
}

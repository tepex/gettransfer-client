package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.RouteInfoRequestEntity
import com.kg.gettransfer.data.model.RouteInfoHourlyRequestEntity

import org.koin.standalone.KoinComponent

interface RouteRemote: KoinComponent {
    suspend fun getRouteInfo(request: RouteInfoRequestEntity): RouteInfoEntity
    suspend fun getRouteInfo(request: RouteInfoHourlyRequestEntity): RouteInfoEntity
}

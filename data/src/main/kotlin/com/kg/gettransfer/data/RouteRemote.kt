package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.RouteInfoRequestEntity
import org.koin.core.KoinComponent

interface RouteRemote : KoinComponent {

    suspend fun getRouteInfo(request: RouteInfoRequestEntity): RouteInfoEntity
}

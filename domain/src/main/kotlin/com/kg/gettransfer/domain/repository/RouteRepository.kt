package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.RouteInfoHourlyRequest

interface RouteRepository {
    suspend fun getRouteInfo(request: RouteInfoRequest): Result<RouteInfo>
    suspend fun getRouteInfo(request: RouteInfoHourlyRequest): Result<RouteInfo>
}

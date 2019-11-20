package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.RouteInfoRequest

interface RouteRepository {
    suspend fun getRouteInfo(request: RouteInfoRequest): Result<RouteInfo>
}

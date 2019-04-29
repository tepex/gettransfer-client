package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo
import java.util.Date

interface RouteRepository {
    suspend fun getRouteInfo(from: Point, to: Point, withPrices: Boolean, returnWay: Boolean, currency: String, dateTime: Date?): Result<RouteInfo>
    suspend fun getRouteInfo(from: Point, hourlyDuration: Int, currency: String, dateTime: Date?): Result<RouteInfo>
}

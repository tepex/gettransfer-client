package com.kg.gettransfer.remote

import com.kg.gettransfer.data.RouteRemote

import com.kg.gettransfer.data.model.RouteInfoEntity

import com.kg.gettransfer.remote.mapper.RouteInfoMapper

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.RouteInfoModel

import org.koin.standalone.get

class RouteRemoteImpl : RouteRemote {
    private val core   = get<ApiCore>()
    private val routeInfoMapper = get<RouteInfoMapper>()

    override suspend fun getRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean, currency: String, dateTime: String?): RouteInfoEntity {
        val response: ResponseModel<RouteInfoModel> = core.tryTwice { core.api.getRouteInfo(points, null, withPrices, returnWay, currency, dateTime) }
        return routeInfoMapper.fromRemote(response.data!!)
    }

    override suspend fun getRouteInfo(points: Array<String>, hourlyDuration: Int, currency: String, dateTime: String?): RouteInfoEntity {
        val response: ResponseModel<RouteInfoModel> = core.tryTwice { core.api.getRouteInfo(points, hourlyDuration, true, false, currency, dateTime) }
        return routeInfoMapper.fromRemote(response.data!!)
    }
}

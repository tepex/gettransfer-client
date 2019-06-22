package com.kg.gettransfer.remote

import com.kg.gettransfer.data.RouteRemote

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.RouteInfoRequestEntity
import com.kg.gettransfer.data.model.RouteInfoHourlyRequestEntity

import com.kg.gettransfer.remote.mapper.RouteInfoMapper

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.RouteInfoModel

import org.koin.standalone.get

class RouteRemoteImpl : RouteRemote {
    private val core   = get<ApiCore>()
    private val routeInfoMapper = get<RouteInfoMapper>()

    override suspend fun getRouteInfo(request: RouteInfoRequestEntity): RouteInfoEntity {
        val response: ResponseModel<RouteInfoModel> = core.tryTwice {
            core.api.getRouteInfo(
                arrayOf(request.from, request.to),
                null,
                request.withPrices,
                request.returnWay,
                request.currency,
                request.dateTime
            )
        }
        return routeInfoMapper.fromRemote(response.data!!)
    }

    override suspend fun getRouteInfo(request: RouteInfoHourlyRequestEntity): RouteInfoEntity {
        val response: ResponseModel<RouteInfoModel> = core.tryTwice {
            core.api.getRouteInfo(
                arrayOf(request.from),
                request.hourlyDuration,
                true,
                false,
                request.currency,
                request.dateTime
            )
        }
        return routeInfoMapper.fromRemote(response.data!!)
    }
}

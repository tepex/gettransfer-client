package com.kg.gettransfer.remote

import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.RouteInfoHourlyRequestEntity
import com.kg.gettransfer.data.model.RouteInfoRequestEntity
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.RouteInfoModel
import com.kg.gettransfer.remote.model.map
import org.koin.core.get

class RouteRemoteImpl : RouteRemote {

    private val core = get<ApiCore>()

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
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun getRouteInfo(request: RouteInfoHourlyRequestEntity): RouteInfoEntity {
        val response: ResponseModel<RouteInfoModel> = core.tryTwice {
            core.api.getRouteInfo(
                arrayOf(request.from),
                request.hourlyDuration,
                withPrices = true,
                returnWay = false,
                currency = request.currency,
                dateTime = request.dateTime
            )
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }
}

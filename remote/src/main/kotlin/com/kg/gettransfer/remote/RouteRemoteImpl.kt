package com.kg.gettransfer.remote

import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.model.RouteInfoEntity
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
                arrayOf(request.from).let { arr -> request.to?.let { arr.plus(it) } ?: arr },
                request.hourlyDuration,
                request.withPrices,
                request.returnWay,
                request.currency,
                request.dateTo,
                request.dateReturn
            )
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }
}

package com.kg.gettransfer.remote

import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.TransportTypePriceEntity

import com.kg.gettransfer.remote.mapper.RouteInfoMapper

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.RouteInfoModel

import org.koin.standalone.get

class RouteRemoteImpl : RouteRemote {
    private val core   = get<ApiCore>()
    private val mapper = get<RouteInfoMapper>()

    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean): RouteInfoEntity {
        val response: ResponseModel<RouteInfoModel> = tryGetRouteInfo(arrayOf(from, to), withPrices, returnWay)
        return mapper.fromRemote(response.data!!)
    }

    private suspend fun tryGetRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean):
        ResponseModel<RouteInfoModel> {
        return try { core.api.getRouteInfo(points, withPrices, returnWay).await() }
        catch (e: Exception) {
            if (e is RemoteException) throw e /* second invocation */
            val ae = core.remoteException(e)
            if (!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch (e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.getRouteInfo(points, withPrices, returnWay).await() } catch (e2: Exception) { throw core.remoteException(e2) }
        }
    }
}

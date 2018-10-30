package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.RouteDataStore

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.RouteInfoEntity

/**
 * Implementation of the [RemoteDataStore] interface to provide a means of communicating with the remote data source
 */
open class RouteRemoteDataStore(private val remote: RouteRemote): RouteDataStore {
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean): RouteInfoEntity? {
        try { return remote.getRouteInfo(from, to, withPrices, returnWay) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
}

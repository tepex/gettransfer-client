package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.RouteDataStore
import com.kg.gettransfer.data.model.RouteInfoEntity

import org.koin.standalone.inject
import java.lang.UnsupportedOperationException

/**
 * Implementation of the [RouteDataStore] interface to provide a means of communicating with the remote data source
 */
open class RouteDataStoreRemote: RouteDataStore {
    private val remote: RouteRemote by inject()
    
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean?, returnWay: Boolean?, currency: String?) =
        remote.getRouteInfo(from, to, withPrices!!, returnWay!!, currency!!)

    override fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity) {
        throw UnsupportedOperationException()
    }
}

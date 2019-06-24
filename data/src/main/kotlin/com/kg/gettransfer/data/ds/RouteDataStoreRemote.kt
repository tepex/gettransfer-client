package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.RouteDataStore
import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.RouteInfoHourlyRequestEntity
import com.kg.gettransfer.data.model.RouteInfoRequestEntity
import org.koin.standalone.inject

/**
 * Implementation of the [RouteDataStore] interface to provide a means of communicating with the remote data source
 */
open class RouteDataStoreRemote : RouteDataStore {

    private val remote: RouteRemote by inject()

    override suspend fun getRouteInfo(request: RouteInfoRequestEntity) = remote.getRouteInfo(request)

    override suspend fun getRouteInfo(request: RouteInfoHourlyRequestEntity) = remote.getRouteInfo(request)

    override suspend fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity) {
        throw UnsupportedOperationException()
    }
}

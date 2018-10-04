package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.RouteDataStore
import com.kg.gettransfer.data.model.RouteInfoEntity

/**
 * Implementation of the [CacheDataStore] interface to provide a means of communicating with the remote data source.
 */
open class RouteCacheDataStore(): RouteDataStore {
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean): RouteInfoEntity {
        throw UnsupportedOperationException()
    }
}

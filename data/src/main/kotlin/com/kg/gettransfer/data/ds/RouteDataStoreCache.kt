package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.RouteCache
import com.kg.gettransfer.data.RouteDataStore

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.RouteInfoRequestEntity
import com.kg.gettransfer.data.model.RouteInfoHourlyRequestEntity

import org.koin.standalone.inject


/**
 * Implementation of the [RouteDataStore] interface to provide a means of communicating with the cache data source.
 */
open class RouteDataStoreCache: RouteDataStore {
    private val cache: RouteCache by inject()

    override suspend fun getRouteInfo(request: RouteInfoRequestEntity): RouteInfoEntity? {
        return try {
            cache.getRouteInfo(request.from, request.to)
        } catch (e: CacheException) { throw e }
    }

    override suspend fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity) {
        try {
            cache.setRouteInfo(from, to, routeInfo)
        } catch (e: CacheException) { throw e }
    }

    override suspend fun getRouteInfo(request: RouteInfoHourlyRequestEntity): RouteInfoEntity? {
        throw UnsupportedOperationException()
    }
}

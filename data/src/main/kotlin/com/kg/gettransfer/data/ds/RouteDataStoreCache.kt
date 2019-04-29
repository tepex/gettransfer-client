package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.RouteCache
import com.kg.gettransfer.data.RouteDataStore

import com.kg.gettransfer.data.model.RouteInfoEntity

import org.koin.standalone.inject
import java.lang.UnsupportedOperationException
import java.util.Date

/**
 * Implementation of the [RouteDataStore] interface to provide a means of communicating with the cache data source.
 */
open class RouteDataStoreCache: RouteDataStore {
    private val cache: RouteCache by inject()

    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean?, returnWay: Boolean?, currency: String?, dateTime: Date?): RouteInfoEntity? {
        return try {
            cache.getRouteInfo(from, to)
        } catch (e: CacheException) { throw e }
    }

    override suspend fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity) {
        try {
            cache.setRouteInfo(from, to, routeInfo)
        } catch (e: CacheException) { throw e }
    }

    override suspend fun getRouteInfo(from: String, hourlyDuration: Int, currency: String, dateTime: Date?): RouteInfoEntity? {
        throw UnsupportedOperationException()
    }
}

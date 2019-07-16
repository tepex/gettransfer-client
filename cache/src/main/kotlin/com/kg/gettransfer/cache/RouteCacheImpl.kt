package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.model.map
import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.RouteCache
import com.kg.gettransfer.data.model.RouteInfoEntity
import org.koin.core.KoinComponent
import org.koin.core.inject

class RouteCacheImpl : RouteCache, KoinComponent {

    private val db: CacheDatabase by inject()

    override suspend fun getRouteInfo(from: String, to: String): RouteInfoEntity? =
        try {
            db.routeCacheDao().getRouteInfo(from, to)?.map()
        } catch (e: IllegalStateException) {
            throw CacheException(CacheException.ILLEGAL_STATE, e.message ?: "IllegalStateException")
        }

    override suspend fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity) {
        try {
            db.routeCacheDao().insert(routeInfo.map(from, to))
        } catch (e: IllegalStateException) {
            throw CacheException(CacheException.ILLEGAL_STATE, e.message ?: "IllegalStateException")
        }
    }
}

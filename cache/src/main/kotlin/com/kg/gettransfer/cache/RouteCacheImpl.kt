package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.mapper.RouteInfoEntityMapper
import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.RouteCache
import com.kg.gettransfer.data.model.RouteInfoEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class RouteCacheImpl: RouteCache, KoinComponent {
    private val db: CacheDatabase by inject()
    private val routeInfoMapper: RouteInfoEntityMapper by inject()

    override fun getRouteInfo(from: String, to: String) = db.routeCacheDao().getRouteInfo(from, to)?.let { routeInfoMapper.fromCached(it) }

    override fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity){
        try {
            db.routeCacheDao().insert(routeInfoMapper.toCached(from, to, routeInfo))
        } catch (e: IllegalStateException) {
            throw CacheException(CacheException.ILLEGAL_STATE, e.message ?: "IllegalStateException")
        }
    }
}
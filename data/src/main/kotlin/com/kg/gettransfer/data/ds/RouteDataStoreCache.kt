package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.RouteCache
import com.kg.gettransfer.data.RouteDataStore

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.RouteInfoEntity

import org.koin.standalone.inject

/**
 * Implementation of the [RouteDataStore] interface to provide a means of communicating with the cache data source.
 */
open class RouteDataStoreCache: RouteDataStore {
    private val cache: RouteCache by inject()
    
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean, currency: String): RouteInfoEntity {
        throw UnsupportedOperationException()
    }
}

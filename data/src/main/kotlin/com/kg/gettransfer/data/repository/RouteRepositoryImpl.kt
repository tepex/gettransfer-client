package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RouteDataStore

import com.kg.gettransfer.data.ds.RouteDataStoreFactory
import com.kg.gettransfer.data.ds.RouteRemoteDataStore

import com.kg.gettransfer.data.mapper.RouteInfoMapper

import com.kg.gettransfer.domain.repository.RouteRepository

class RouteRepositoryImpl(private val factory: RouteDataStoreFactory,
                          private val mapper: RouteInfoMapper): RouteRepository {
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean) =
        mapper.fromEntity(factory.retrieveRemoteDataStore().getRouteInfo(from, to, withPrices, returnWay))
}

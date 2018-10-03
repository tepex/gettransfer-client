package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RouteDataStore
import com.kg.gettransfer.data.mapper.RouteInfoMapper

import com.kg.gettransfer.domain.repository.RouteRepository

class RouteRepositoryImpl(private val dataStore: RouteDataStore,
                          private val mapper: RouteInfoMapper): RouteRepository {
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean) =
        mapper.fromEntity(dataStore.getRouteInfo(from, to, withPrices, returnWay))
}

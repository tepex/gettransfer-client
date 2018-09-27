package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.repository.RouteRepository

class RouteRepositoryImpl(private val apiRepository: ApiRepositoryImpl): RouteRepository {
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean) =
        apiRepository.getRouteInfo(from, to, withPrices, returnWay)
}

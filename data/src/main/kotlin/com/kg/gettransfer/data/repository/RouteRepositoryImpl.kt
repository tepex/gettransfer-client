package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.repository.ApiRepository
import com.kg.gettransfer.domain.repository.RouteRepository

class RouteRepositoryImpl(private val apiRepository: ApiRepository): RouteRepository {
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean) =
        apiRepository.getRouteInfo(from, to, withPrices, returnWay)
}

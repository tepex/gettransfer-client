package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RouteDataStore

import com.kg.gettransfer.data.ds.RouteDataStoreFactory
import com.kg.gettransfer.data.ds.RouteRemoteDataStore

import com.kg.gettransfer.data.mapper.PointMapper
import com.kg.gettransfer.data.mapper.RouteInfoMapper

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.domain.repository.RouteRepository

class RouteRepositoryImpl(private val factory: RouteDataStoreFactory,
                          private val routeInfoMapper: RouteInfoMapper,
                          private val pointMapper: PointMapper): RouteRepository {


    override suspend fun getRouteInfo(from: Point, to: Point, withPrices: Boolean, returnWay: Boolean): RouteInfo {
        val fromString = pointMapper.toEntity(from)
        val toString = pointMapper.toEntity(to)
        return routeInfoMapper.fromEntity(factory.retrieveRemoteDataStore().getRouteInfo(fromString, toString, withPrices, returnWay))
    }

}

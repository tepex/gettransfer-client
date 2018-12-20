package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.RouteDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.RouteDataStoreCache
import com.kg.gettransfer.data.ds.RouteDataStoreRemote

import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.PointMapper
import com.kg.gettransfer.data.mapper.RouteInfoMapper

import com.kg.gettransfer.data.model.RouteInfoEntity

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportTypePrice

import com.kg.gettransfer.domain.repository.RouteRepository

import org.koin.standalone.get

class RouteRepositoryImpl(
    private val factory: DataStoreFactory<RouteDataStore, RouteDataStoreCache, RouteDataStoreRemote>
) : BaseRepository(), RouteRepository {

    private val routeInfoMapper = get<RouteInfoMapper>()
    private val pointMapper     = get<PointMapper>()

    override suspend fun getRouteInfo(from: Point, to: Point, withPrices: Boolean, returnWay: Boolean): Result<RouteInfo> =
        retrieveRemoteModel<RouteInfoEntity, RouteInfo>(routeInfoMapper, DEFAULT) {
            factory.retrieveRemoteDataStore().getRouteInfo(
                pointMapper.toEntity(from),
                pointMapper.toEntity(to),
                withPrices,
                returnWay
            )
    }

    companion object {
        private val DEFAULT = RouteInfo(
            success          = false,
            distance         = null,
            duration         = null,
            prices           = emptyMap<TransportType.ID, TransportTypePrice>(),
            watertaxi        = false,
            polyLines        = emptyList<String>(),
            overviewPolyline = null
        )
    }
}

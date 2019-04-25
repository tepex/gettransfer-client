package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.RouteDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.RouteDataStoreCache
import com.kg.gettransfer.data.ds.RouteDataStoreRemote

import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.PointMapper
import com.kg.gettransfer.data.mapper.RouteInfoMapper
import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.data.model.RouteInfoEntity

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportTypePrice

import com.kg.gettransfer.domain.repository.RouteRepository

import org.koin.standalone.get
import java.util.Date

class RouteRepositoryImpl(
    private val factory: DataStoreFactory<RouteDataStore, RouteDataStoreCache, RouteDataStoreRemote>
) : BaseRepository(), RouteRepository {

    private val routeInfoMapper = get<RouteInfoMapper>()
    private val pointMapper     = get<PointMapper>()

    override suspend fun getRouteInfo(from: Point, to: Point, withPrices: Boolean, returnWay: Boolean, currency: String, dateTime: Date?): Result<RouteInfo> {
            val fromEntity = pointMapper.toEntity(from)
            val toEntity = pointMapper.toEntity(to)
            val result: ResultEntity<RouteInfoEntity?> = retrieveEntity/*(routeInfoMapper, DEFAULT)*/ { fromRemote ->
                factory.retrieveDataStore(fromRemote).getRouteInfo(
                        fromEntity,
                        toEntity,
                        withPrices,
                        returnWay,
                        currency,
                        dateTime
                )
            }
            try {
                result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().setRouteInfo(fromEntity, toEntity, it) }
            } catch (e: CacheException){
                return Result(result.entity?.let { routeInfoMapper.fromEntity(it) } ?: DEFAULT, null, false, ExceptionMapper.map(e))
            }
            return Result(result.entity?.let { routeInfoMapper.fromEntity(it) } ?: DEFAULT,
                    result.error?.let { ExceptionMapper.map(it) }, result.error != null && result.entity != null,
                    result.cacheError?.let { ExceptionMapper.map(it) })
    }

    override suspend fun getRouteInfo(from: Point, hourlyDuration: Int, currency: String, dateTime: Date?): Result<RouteInfo> {
        val fromEntity = pointMapper.toEntity(from)
        val result: ResultEntity<RouteInfoEntity?> = retrieveRemoteEntity /*(routeInfoMapper, DEFAULT)*/ {
            factory.retrieveRemoteDataStore().getRouteInfo(
                    fromEntity,
                    hourlyDuration,
                    currency,
                    dateTime
            )
        }
        return Result(result.entity?.let { routeInfoMapper.fromEntity(it) } ?: DEFAULT,
                result.error?.let { ExceptionMapper.map(it) }, result.error != null && result.entity != null,
                result.cacheError?.let { ExceptionMapper.map(it) })
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

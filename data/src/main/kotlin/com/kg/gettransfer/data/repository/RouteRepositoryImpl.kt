package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.RouteDataStore
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.RouteDataStoreCache
import com.kg.gettransfer.data.ds.RouteDataStoreRemote
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.map
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.RouteInfoHourlyRequest
import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.repository.RouteRepository
import org.koin.standalone.get
import java.text.DateFormat

class RouteRepositoryImpl(
    private val factory: DataStoreFactory<RouteDataStore, RouteDataStoreCache, RouteDataStoreRemote>
) : BaseRepository(), RouteRepository {

    private val dateFormatTZ = get<ThreadLocal<DateFormat>>("iso_date_TZ")

    override suspend fun getRouteInfo(request: RouteInfoRequest): Result<RouteInfo> {

        val requestEntity = request.map(dateFormatTZ.get())
        val result: ResultEntity<RouteInfoEntity?> = retrieveEntity/*(routeInfoMapper, DEFAULT)*/ { fromRemote ->
            factory.retrieveDataStore(fromRemote).getRouteInfo(requestEntity)
        }
        try {
            result.entity?.let { entity ->
                result.error ?: factory.retrieveCacheDataStore().setRouteInfo(
                    requestEntity.from,
                    requestEntity.to,
                    entity
                )
            }
        } catch (e: CacheException) {
            return Result(
                result.entity?.map() ?: RouteInfo.EMPTY,
                null,
                false,
                e.map()
            )
        }
        return Result(
            result.entity?.map() ?: RouteInfo.EMPTY,
            result.error?.map(),
            result.error != null && result.entity != null,
            result.cacheError?.map()
        )
    }

    override suspend fun getRouteInfo(request: RouteInfoHourlyRequest): Result<RouteInfo> {

        val result: ResultEntity<RouteInfoEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().getRouteInfo(request.map(dateFormatTZ.get()))
        }
        return Result(
            result.entity?.map() ?: RouteInfo.EMPTY,
            result.error?.map(),
            result.error != null && result.entity != null,
            result.cacheError?.map()
        )
    }
}

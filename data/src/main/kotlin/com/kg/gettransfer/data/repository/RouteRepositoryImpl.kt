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
import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.repository.RouteRepository
import org.koin.core.get
import org.koin.core.qualifier.named
import java.text.DateFormat

class RouteRepositoryImpl(
    private val factory: DataStoreFactory<RouteDataStore, RouteDataStoreCache, RouteDataStoreRemote>
) : BaseRepository(), RouteRepository {

    private val dateFormatTZ = get<ThreadLocal<DateFormat>>(named("iso_date_TZ"))

    override suspend fun getRouteInfo(request: RouteInfoRequest): Result<RouteInfo> {

        val requestEntity = request.map(dateFormatTZ.get())
        val result: ResultEntity<RouteInfoEntity?> = if (requestEntity.to != null) {
            retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getRouteInfo(requestEntity)
            }
        } else {
            retrieveRemoteEntity {
                factory.retrieveRemoteDataStore().getRouteInfo(requestEntity)
            }
        }
        try {
            requestEntity.to?.let { to ->
                result.entity?.let { entity ->
                    result.error ?: factory.retrieveCacheDataStore().setRouteInfo(
                        requestEntity.from,
                        to,
                        entity
                    )
                }
            }
        } catch (e: CacheException) {
            return getResult(result)
        }
        return getResult(result)
    }

    private fun getResult(result: ResultEntity<RouteInfoEntity?>) =
        Result(
            result.entity?.map() ?: RouteInfo.EMPTY,
            result.error?.map(),
            result.error != null && result.entity != null,
            result.cacheError?.map()
        )
}

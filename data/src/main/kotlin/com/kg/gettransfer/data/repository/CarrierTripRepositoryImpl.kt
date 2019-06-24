package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.CarrierTripDataStore
import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.ds.CarrierTripDataStoreCache
import com.kg.gettransfer.data.ds.CarrierTripDataStoreRemote
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.map
import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.CarrierTripRepository
import org.koin.standalone.get
import java.text.DateFormat

class CarrierTripRepositoryImpl(
    private val factory: DataStoreFactory<CarrierTripDataStore, CarrierTripDataStoreCache, CarrierTripDataStoreRemote>,
    private val preferencesCache: PreferencesCache
) : BaseRepository(), CarrierTripRepository {

    private val dateFormat = get<ThreadLocal<DateFormat>>("iso_date")

    override var backGroundCoordinates: Int
        get() = preferencesCache.driverCoordinatesInBackGround
        set(value) {
            preferencesCache.driverCoordinatesInBackGround = value
        }

    override suspend fun getCarrierTrips(): Result<List<CarrierTripBase>> {
        val result: ResultEntity<List<CarrierTripBaseEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getCarrierTrips()
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addAllCarrierTrips(it) }
        return Result(
            result.entity?.map { it.map(dateFormat.get()) } ?: emptyList(),
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override suspend fun getCarrierTrip(id: Long): Result<CarrierTrip> {
        val result: ResultEntity<CarrierTripEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getCarrierTrip(id)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addCarrierTrip(it) }
        return Result(
            result.entity?.map(dateFormat.get()) ?: CarrierTrip.EMPTY,
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override suspend fun getCarrierTripCached(id: Long): Result<CarrierTrip> {
        val result: ResultEntity<CarrierTripEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getCarrierTrip(id)
        }
        return Result(
            result.entity?.map(dateFormat.get()) ?: CarrierTrip.EMPTY,
            null,
            result.entity != null,
            result.cacheError?.map()
        )
    }

    override suspend fun clearCarrierTripsCache() {
        factory.retrieveCacheDataStore().clearCariierTripsCache()
    }
}

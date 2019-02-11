package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.mapper.CarrierTripBaseEntityMapper
import com.kg.gettransfer.cache.mapper.CarrierTripEntityMapper
import com.kg.gettransfer.data.CarrierTripCache
import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class CarrierTripCacheImpl: CarrierTripCache, KoinComponent {
    private val db: CacheDatabase by inject()
    private val carrierTripMapper: CarrierTripEntityMapper by inject()
    private val carrierTripBaseMapper: CarrierTripBaseEntityMapper by inject()

    override suspend fun insertAllBaseCarrierTrips(trips: List<CarrierTripBaseEntity>) {
        val tripsCached = trips.map { carrierTripBaseMapper.toCached(it) }
        db.carrierTripCachedDao().insertAllCarrierTripsBase(tripsCached)
    }

    override suspend fun insertCarrierTrip(trip: CarrierTripEntity) {
        val carrierTripCached = carrierTripMapper.toCached(trip)
        db.carrierTripCachedDao().insertCarrierTripBase(carrierTripCached.base)
        db.carrierTripCachedDao().insertCarrierTripMore(carrierTripCached.more)
    }

    override suspend fun getAllBaseCarrierTrips(): List<CarrierTripBaseEntity> {
        return db.carrierTripCachedDao().getAllCarrierTripsBase().map { carrierTripBaseMapper.fromCached(it) }
    }

    override suspend fun getCarrierTrip(id: Long): CarrierTripEntity {
        val carrierTripBaseCached = db.carrierTripCachedDao().getCarrierTripBase(id)
        val carrierTripMoreCached = db.carrierTripCachedDao().getCarrierTripMore(id)
        return carrierTripMapper.fromCached(carrierTripBaseCached, carrierTripMoreCached)
    }

    override suspend fun deleteAllCarrierTrips() {
        db.carrierTripCachedDao().deleteAllCarrierTripsBase()
        db.carrierTripCachedDao().deleteAllCarrierTripsMore()
    }
}
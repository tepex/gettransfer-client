package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.model.map
import com.kg.gettransfer.data.CarrierTripCache
import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class CarrierTripCacheImpl : CarrierTripCache, KoinComponent {

    private val db: CacheDatabase by inject()

    override suspend fun insertAllBaseCarrierTrips(trips: List<CarrierTripBaseEntity>) {
        val tripsCached = trips.map { it.map() }
        db.carrierTripCachedDao().insertAllCarrierTripsBase(tripsCached)
    }

    override suspend fun insertCarrierTrip(trip: CarrierTripEntity) {
        val carrierTripCached = trip.map()
        db.carrierTripCachedDao().insertCarrierTripBase(carrierTripCached.base)
        db.carrierTripCachedDao().insertCarrierTripMore(carrierTripCached.more)
    }

    override suspend fun getAllBaseCarrierTrips(): List<CarrierTripBaseEntity> {
        return db.carrierTripCachedDao().getAllCarrierTripsBase().map { it.map() }
    }

    override suspend fun getCarrierTrip(id: Long): CarrierTripEntity =
        db.carrierTripCachedDao().getCarrierTripBase(id).map(db.carrierTripCachedDao().getCarrierTripMore(id))

    override suspend fun deleteAllCarrierTrips() {
        db.carrierTripCachedDao().deleteAllCarrierTripsBase()
        db.carrierTripCachedDao().deleteAllCarrierTripsMore()
    }
}

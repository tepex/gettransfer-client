package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.CarrierTripCache
import com.kg.gettransfer.data.CarrierTripDataStore

/**
 * Implementation of the [CarrierTripDataStore] interface to provide a means of communicating with the local data source.
 */
open class CarrierTripCacheDataStore(/*private val cache: CarrierTripCache*/): CarrierTripDataStore {
    override suspend fun getCarrierTrips(): List<CarrierTripEntity> {
        /*= cache.getCarrierTrips()*/
        throw UnsupportedOperationException()
    }
    
    override suspend fun getCarrierTrip(id: Long): CarrierTripEntity {
        /*= cache.getCarrierTrip(id)*/
        throw UnsupportedOperationException()
    }
}

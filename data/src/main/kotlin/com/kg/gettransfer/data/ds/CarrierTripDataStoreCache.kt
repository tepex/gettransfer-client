package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.CarrierTripCache
import com.kg.gettransfer.data.CarrierTripDataStore

import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity

import org.koin.core.inject

/**
 * Implementation of the [CarrierTripDataStore] interface to provide
 * a means of communicating with the local data source.
 */
open class CarrierTripDataStoreCache : CarrierTripDataStore {

    private val cache: CarrierTripCache by inject()

    suspend fun addAllCarrierTrips(trips: List<CarrierTripBaseEntity>) = cache.insertAllBaseCarrierTrips(trips)
    suspend fun addCarrierTrip(trip: CarrierTripEntity) = cache.insertCarrierTrip(trip)

    override suspend fun getCarrierTrips() = cache.getAllBaseCarrierTrips()
    override suspend fun getCarrierTrip(id: Long) = cache.getCarrierTrip(id)

    suspend fun clearCariierTripsCache() = cache.deleteAllCarrierTrips()
}

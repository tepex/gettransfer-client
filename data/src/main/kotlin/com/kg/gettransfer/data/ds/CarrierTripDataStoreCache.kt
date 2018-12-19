package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.CarrierTripCache
import com.kg.gettransfer.data.CarrierTripDataStore

import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity

import org.koin.standalone.inject

/**
 * Implementation of the [CarrierTripDataStore] interface to provide a means of communicating with the local data source.
 */
open class CarrierTripDataStoreCache: CarrierTripDataStore {
    private val cache: CarrierTripCache by inject()

    override suspend fun getCarrierTrips(): List<CarrierTripBaseEntity> {
        /*= cache.getCarrierTrips()*/
        throw UnsupportedOperationException()
    }

    override suspend fun getCarrierTrip(id: Long): CarrierTripEntity {
        /*= cache.getCarrierTrip(id)*/
        throw UnsupportedOperationException()
    }
}

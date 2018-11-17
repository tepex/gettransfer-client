package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.CarrierTripDataStore

import org.koin.standalone.inject

/**
 * Implementation of the [CarrierTripDataStore] interface to provide a means of communicating with the remote data source.
 */
open class CarrierTripDataStoreRemote: CarrierTripDataStore {
    private val remote: CarrierTripRemote by inject()

    override suspend fun getCarrierTrips() = remote.getCarrierTrips()
    override suspend fun getCarrierTrip(id: Long) = remote.getCarrierTrip(id)
}

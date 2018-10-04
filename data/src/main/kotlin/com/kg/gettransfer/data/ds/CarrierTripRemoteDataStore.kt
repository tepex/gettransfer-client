package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.CarrierTripDataStore

/**
 * Implementation of the [CarrierTripDataStore] interface to provide a means of communicating with the remote data source.
 */
open class CarrierTripRemoteDataStore(private val remote: CarrierTripRemote): CarrierTripDataStore {
    override suspend fun getCarrierTrips() = remote.getCarrierTrips()
    override suspend fun getCarrierTrip(id: Long) = remote.getCarrierTrip(id)
}

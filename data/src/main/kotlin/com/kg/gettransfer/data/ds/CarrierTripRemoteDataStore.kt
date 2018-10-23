package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.CarrierTripDataStore
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.CarrierTripEntity

/**
 * Implementation of the [CarrierTripDataStore] interface to provide a means of communicating with the remote data source.
 */
open class CarrierTripRemoteDataStore(private val remote: CarrierTripRemote): CarrierTripDataStore {
    override suspend fun getCarrierTrips(): List<CarrierTripEntity> {
        try { return remote.getCarrierTrips() }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    override suspend fun getCarrierTrip(id: Long): CarrierTripEntity {
        try { return remote.getCarrierTrip(id) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
}

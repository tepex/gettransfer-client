package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ds.CarrierTripDataStoreFactory

import com.kg.gettransfer.data.mapper.CarrierTripMapper

import com.kg.gettransfer.domain.model.CarrierTrip

import com.kg.gettransfer.domain.repository.CarrierTripRepository

class CarrierTripRepositoryImpl(private val factory: CarrierTripDataStoreFactory,
                                private val mapper: CarrierTripMapper): CarrierTripRepository {
    override suspend fun getCarrierTrips(): List<CarrierTrip> {
        return factory.retrieveRemoteDataStore().getCarrierTrips().map { mapper.fromEntity(it) }
    }
    
    override suspend fun getCarrierTrip(id: Long): CarrierTrip {
        return mapper.fromEntity(factory.retrieveRemoteDataStore().getCarrierTrip(id))
    }
}

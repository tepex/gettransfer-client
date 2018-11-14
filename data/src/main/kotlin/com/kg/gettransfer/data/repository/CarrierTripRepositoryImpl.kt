package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.CarrierTripDataStore

import com.kg.gettransfer.data.ds.CarrierTripDataStoreCache
import com.kg.gettransfer.data.ds.CarrierTripDataStoreRemote
import com.kg.gettransfer.data.ds.DataStoreFactory

import com.kg.gettransfer.data.mapper.CarrierTripMapper
import com.kg.gettransfer.data.model.CarrierTripEntity

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.CarrierTripRepository

class CarrierTripRepositoryImpl(private val factory: DataStoreFactory<CarrierTripDataStore, CarrierTripDataStoreCache, CarrierTripDataStoreRemote>,
                                private val mapper: CarrierTripMapper): BaseRepository(), CarrierTripRepository {
    override suspend fun getCarrierTrips(): Result<List<CarrierTrip>> =
        retrieveRemoteListModel<CarrierTripEntity, CarrierTrip>(mapper) { factory.retrieveRemoteDataStore().getCarrierTrips() }
        
    override suspend fun getCarrierTrip(id: Long): Result<CarrierTrip> =
        retrieveRemoteModel<CarrierTripEntity, CarrierTrip>(mapper) { factory.retrieveRemoteDataStore().getCarrierTrip(id) }
}

package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.CarrierTripDataStore

import com.kg.gettransfer.data.ds.CarrierTripDataStoreCache
import com.kg.gettransfer.data.ds.CarrierTripDataStoreRemote
import com.kg.gettransfer.data.ds.DataStoreFactory

import com.kg.gettransfer.data.mapper.CarrierTripMapper
import com.kg.gettransfer.data.model.CarrierTripEntity

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.VehicleBase

import com.kg.gettransfer.domain.repository.CarrierTripRepository

import java.util.Date

import org.koin.standalone.get

class CarrierTripRepositoryImpl(private val factory: DataStoreFactory<CarrierTripDataStore, CarrierTripDataStoreCache, CarrierTripDataStoreRemote>):
                            BaseRepository(), CarrierTripRepository {
    private val mapper = get<CarrierTripMapper>()

    override suspend fun getCarrierTrips(): Result<List<CarrierTrip>> =
        retrieveRemoteListModel<CarrierTripEntity, CarrierTrip>(mapper) { factory.retrieveRemoteDataStore().getCarrierTrips() }

    override suspend fun getCarrierTrip(id: Long): Result<CarrierTrip> =
        retrieveRemoteModel<CarrierTripEntity, CarrierTrip>(mapper, defaultModel) {
            factory.retrieveRemoteDataStore().getCarrierTrip(id)
        }

    companion object {
        private val defaultModel = 
            CarrierTrip(0,
                    0,
                    CityPoint(null, null, null),
                    CityPoint(null, null, null),
                    Date(),
                    null,
                    null,
                    0,
                    0,
                    null,
                    false,
                    "",
                    VehicleBase("", ""),
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null)
        }
}

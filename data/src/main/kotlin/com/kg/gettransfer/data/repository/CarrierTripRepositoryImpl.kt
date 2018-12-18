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
import com.kg.gettransfer.domain.model.VehicleInfo

import com.kg.gettransfer.domain.repository.CarrierTripRepository

import java.util.Date

import org.koin.standalone.get

class CarrierTripRepositoryImpl(
    private val factory: DataStoreFactory<CarrierTripDataStore, CarrierTripDataStoreCache, CarrierTripDataStoreRemote>
) : BaseRepository(), CarrierTripRepository {
    private val mapper = get<CarrierTripMapper>()

    override suspend fun getCarrierTrips(): Result<List<CarrierTrip>> =
        retrieveRemoteListModel<CarrierTripEntity, CarrierTrip>(mapper) { factory.retrieveRemoteDataStore().getCarrierTrips() }

    override suspend fun getCarrierTrip(id: Long): Result<CarrierTrip> =
        retrieveRemoteModel<CarrierTripEntity, CarrierTrip>(mapper, DEFAULT) {
            factory.retrieveRemoteDataStore().getCarrierTrip(id)
        }

    companion object {
        private val DEFAULT =
            CarrierTrip(
                id                    = 0,
                transferId            = 0,
                from                  = CityPoint(null, null, null),
                to                    = CityPoint(null, null, null),
                dateLocal             = Date(),
                duration              = null,
                distance              = null,
                time                  = 0,
                childSeats            = 0,
                childSeatsInfant      = 0,
                childSeatsConvertible = 0,
                childSeatsBooster     = 0,
                comment               = null,
                waterTaxi             = false,
                price                 = "",
                vehicle               = VehicleInfo("", ""),
                pax                   = 0,
                nameSign              = null,
                flightNumber          = null,
                paidSum               = null,
                remainToPay           = null,
                paidPercentage        = null,
                passengerAccount      = null
            )
        }
}

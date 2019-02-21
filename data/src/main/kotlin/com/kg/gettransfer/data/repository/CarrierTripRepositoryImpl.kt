package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.CarrierTripDataStore
import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.data.ds.CarrierTripDataStoreCache
import com.kg.gettransfer.data.ds.CarrierTripDataStoreRemote
import com.kg.gettransfer.data.ds.DataStoreFactory

import com.kg.gettransfer.data.mapper.CarrierTripBaseMapper
import com.kg.gettransfer.data.mapper.CarrierTripMapper
import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.PassengerAccount
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.VehicleInfo

import com.kg.gettransfer.domain.repository.CarrierTripRepository

import java.util.Date

import org.koin.standalone.get

class CarrierTripRepositoryImpl(
    private val factory: DataStoreFactory<CarrierTripDataStore, CarrierTripDataStoreCache, CarrierTripDataStoreRemote>,
    private val preferencesCache: PreferencesCache
) : BaseRepository(), CarrierTripRepository {
    private val carrierTripMapper     = get<CarrierTripMapper>()
    private val carrierTripBaseMapper = get<CarrierTripBaseMapper>()

    override var backGroundCoordinates: Int
        get() = preferencesCache.driverCoordinatesInBackGround
        set(value) { preferencesCache.driverCoordinatesInBackGround = value }

    override suspend fun getCarrierTrips(): Result<List<CarrierTripBase>> {
        /*retrieveRemoteListModel<CarrierTripBaseEntity, CarrierTripBase>(carrierTripBaseMapper) {
            factory.retrieveRemoteDataStore().getAllBaseCarrierTrips()
        }*/
        val result: ResultEntity<List<CarrierTripBaseEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getCarrierTrips()
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addAllCarrierTrips(it) }
        return Result(result.entity?.map { carrierTripBaseMapper.fromEntity(it) }?: emptyList(),
                result.error?.let { ExceptionMapper.map(it) }, result.error != null && result.entity != null)
    }

    override suspend fun getCarrierTrip(id: Long): Result<CarrierTrip> {
        /*retrieveRemoteModel<CarrierTripEntity, CarrierTrip>(carrierTripMapper, DEFAULT) {
            factory.retrieveRemoteDataStore().getCarrierTrip(id)
        }*/
        val result: ResultEntity<CarrierTripEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getCarrierTrip(id)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addCarrierTrip(it) }
        return Result(result.entity?.let { carrierTripMapper.fromEntity(it) }?: DEFAULT,
                result.error?.let { ExceptionMapper.map(it) }, result.error != null && result.entity != null)
    }

    override suspend fun clearCarrierTripsCache() {
        factory.retrieveCacheDataStore().clearCariierTripsCache()
    }

    companion object {
        private val DEFAULT =
            CarrierTrip(
                CarrierTripBase(
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
                    vehicle               = VehicleInfo("", "")
                ),
                pax              = 0,
                nameSign         = null,
                flightNumber     = null,
                paidSum          = "",
                remainsToPay     = "",
                paidPercentage   = 0,
                passengerAccount = PassengerAccount(
                    id       = 0,
                    profile  = Profile(null, null, null),
                    lastSeen = Date()
                )
            )
        }
}

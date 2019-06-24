package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity
import org.koin.standalone.KoinComponent

interface CarrierTripCache : KoinComponent {

    suspend fun insertAllBaseCarrierTrips(trips: List<CarrierTripBaseEntity>)

    suspend fun insertCarrierTrip(trip: CarrierTripEntity)

    suspend fun getAllBaseCarrierTrips(): List<CarrierTripBaseEntity>

    suspend fun getCarrierTrip(id: Long): CarrierTripEntity

    suspend fun deleteAllCarrierTrips()
}

package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity
import org.koin.standalone.KoinComponent

interface CarrierTripRemote : KoinComponent {

    suspend fun getCarrierTrips(): List<CarrierTripBaseEntity>

    suspend fun getCarrierTrip(id: Long): CarrierTripEntity
}

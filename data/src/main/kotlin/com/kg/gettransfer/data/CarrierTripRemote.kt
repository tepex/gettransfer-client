package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.CarrierTripEntity

interface CarrierTripRemote {
    suspend fun getCarrierTrips(): List<CarrierTripEntity>
    suspend fun getCarrierTrip(id: Long): CarrierTripEntity
}

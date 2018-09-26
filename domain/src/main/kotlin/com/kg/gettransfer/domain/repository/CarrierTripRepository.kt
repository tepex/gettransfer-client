package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.CarrierTrip

interface CarrierTripRepository {
    suspend fun getCarrierTrips(): List<CarrierTrip>
    suspend fun getCarrierTrip(id: Long): CarrierTrip
}

package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.domain.model.Result

interface CarrierTripRepository {
    suspend fun getCarrierTrips(): Result<List<CarrierTripBase>>
    suspend fun getCarrierTrip(id: Long): Result<CarrierTrip>
}

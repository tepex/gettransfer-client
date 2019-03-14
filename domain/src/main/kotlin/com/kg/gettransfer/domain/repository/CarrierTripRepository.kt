package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.domain.model.Result

interface CarrierTripRepository {
    var backGroundCoordinates: Int
    suspend fun getCarrierTrips(): Result<List<CarrierTripBase>>
    suspend fun getCarrierTrip(id: Long): Result<CarrierTrip>

    suspend fun clearCarrierTripsCache()

    companion object {
        const val BG_COORDINATES_NOT_ASKED = 0
    }
}

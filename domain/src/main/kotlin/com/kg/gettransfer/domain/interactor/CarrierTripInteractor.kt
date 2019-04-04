package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.CarrierTripRepository

class CarrierTripInteractor(private val repository: CarrierTripRepository) {

    var bgCoordinatesPermission: Int
        get() = repository.backGroundCoordinates
        private set(value) { repository.backGroundCoordinates = value }

    fun permissionChanged(accepted: Boolean) {
        bgCoordinatesPermission =
                if (accepted) BG_COORDINATES_ACCEPTED
                else BG_COORDINATES_REJECTED
    }

    suspend fun getCarrierTrips() = repository.getCarrierTrips()
    suspend fun getCarrierTrip(id: Long, fromCache: Boolean = false) =
            when (fromCache) {
                false -> repository.getCarrierTrip(id)
                true -> repository.getCarrierTripCached(id)
            }

    suspend fun clearCarrierTripsCache(): Result<Unit> {
        repository.clearCarrierTripsCache()
        return Result(Unit)
    }

    companion object {
        const val BG_COORDINATES_ACCEPTED  = 1
        const val BG_COORDINATES_REJECTED  = -1
    }
}
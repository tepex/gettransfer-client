package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.CarrierTripRepository

class CarrierTripInteractor(private val repository: CarrierTripRepository) {
    suspend fun getCarrierTrips() = repository.getCarrierTrips()
    suspend fun getCarrierTrip(id: Long) = repository.getCarrierTrip(id)

    suspend fun clearCarrierTripsCache(): Result<Unit> {
        repository.claerCarrierTripsCache()
        return Result(Unit)
    }
}
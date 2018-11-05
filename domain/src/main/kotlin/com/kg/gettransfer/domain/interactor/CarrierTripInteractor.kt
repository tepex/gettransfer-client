package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.CarrierTripRepository

class CarrierTripInteractor(private val repository: CarrierTripRepository) {
    var selectedTripId: Long? = null

    suspend fun getCarrierTrips() = repository.getCarrierTrips()
    suspend fun getCarrierTrip(id: Long) = repository.getCarrierTrip(id)
}
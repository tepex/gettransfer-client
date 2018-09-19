package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.repository.ApiRepository

class CarrierTripInteractor(private val repository: ApiRepository) {
    var selectedTripId: Long? = null

    suspend fun getCarrierTrips(): List<CarrierTrip> = repository.getCarrierTrips()
    suspend fun getCarrierTrip(carrierTripId: Long): CarrierTrip = repository.getCarrierTrip(carrierTripId)
}
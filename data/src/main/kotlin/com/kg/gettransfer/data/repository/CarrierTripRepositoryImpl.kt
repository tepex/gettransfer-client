package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.repository.CarrierTripRepository

class CarrierTripRepositoryImpl(private val apiRepository: ApiRepositoryImpl): CarrierTripRepository {
    override suspend fun getCarrierTrips() = apiRepository.getCarrierTrips()
    override suspend fun getCarrierTrip(id: Long) = apiRepository.getCarrierTrip(id)
}

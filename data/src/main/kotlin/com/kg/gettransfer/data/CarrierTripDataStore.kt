package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.CarrierTripEntity

import org.koin.standalone.KoinComponent

interface CarrierTripDataStore: KoinComponent {
    suspend fun getCarrierTrips(): List<CarrierTripEntity>
    suspend fun getCarrierTrip(id: Long): CarrierTripEntity
}

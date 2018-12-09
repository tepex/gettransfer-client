package com.kg.gettransfer.domain.model

data class Vehicle(
    val vehicleBase: VehicleBase,
    val year: Int,
    val color: String?,
    val transportType: TransportType,
    var photos: List<String>
)

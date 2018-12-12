package com.kg.gettransfer.domain.model

data class Vehicle(
    override val id: Long,
    val vehicleBase: VehicleBase,
    val year: Int,
    val color: String?,
    val transportType: TransportType,
    var photos: List<String>
) : Entity()

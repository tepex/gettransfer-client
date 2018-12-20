package com.kg.gettransfer.domain.model

data class Vehicle(
    override val id: Long,
    val name: String,
    val registrationNumber: String,
    val year: Int,
    val color: String?,
    val transportType: TransportType,
    var photos: List<String>
) : Entity()

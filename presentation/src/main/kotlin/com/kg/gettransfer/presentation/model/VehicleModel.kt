package com.kg.gettransfer.presentation.model

data class VehicleModel(
    val id: Long,
    val name: String,
    val registrationNumber: String,
    val year: Int,
    val color: String?,
    val transportType: TransportTypeModel,
    val photos: List<String>
)

package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.Vehicle

data class VehicleModel(
    val id: Long,
    val name: String,
    val model: String,
    val registrationNumber: String?,
    val year: Int,
    val color: String?,
    val transportType: TransportTypeModel,
    val photos: List<String>
)

fun Vehicle.map() =
    VehicleModel(
        id,
        name,
        model,
        registrationNumber,
        year,
        color,
        transportType.map(),
        photos
    )

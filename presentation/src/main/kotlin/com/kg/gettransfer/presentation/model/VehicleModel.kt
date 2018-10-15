package com.kg.gettransfer.presentation.model

class VehicleModel(val vehicleBase: VehicleBaseModel,
                   val year: Int,
                   val color: String,
                   val transportType: TransportTypeModel,
                   val photos: List<String>)

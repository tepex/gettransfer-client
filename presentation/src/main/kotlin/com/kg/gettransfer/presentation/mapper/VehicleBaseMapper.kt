package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.VehicleBase

import com.kg.gettransfer.presentation.model.VehicleBaseModel

open class VehicleBaseMapper : Mapper<VehicleBaseModel, VehicleBase> {
    override fun toView(type: VehicleBase) = VehicleBaseModel(type.name, type.registrationNumber)
    override fun fromView(type: VehicleBaseModel) = VehicleBase(type.name, type.registrationNumber)
}

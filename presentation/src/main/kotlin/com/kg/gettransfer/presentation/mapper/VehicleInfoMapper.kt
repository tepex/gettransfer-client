package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.VehicleInfo

import com.kg.gettransfer.presentation.model.VehicleInfoModel

open class VehicleInfoMapper : Mapper<VehicleInfoModel, VehicleInfo> {
    override fun toView(type: VehicleInfo) =
        VehicleInfoModel(
            name               = type.name,
            registrationNumber = type.registrationNumber
        )

    override fun fromView(type: VehicleInfoModel) =
        VehicleInfo(
            name               = type.name,
            registrationNumber = type.registrationNumber
        )
}

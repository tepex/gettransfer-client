package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.presentation.model.DistanceUnitModel

open class DistanceUnitMapper : Mapper<DistanceUnitModel, DistanceUnit> {
    override fun toView(type: DistanceUnit) = DistanceUnitModel(type)
    override fun fromView(type: DistanceUnitModel): DistanceUnit { throw UnsupportedOperationException() }
}

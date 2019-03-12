package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.presentation.model.CityPointModel
import org.koin.standalone.get

class CityPointMapper : Mapper<CityPointModel, CityPoint> {
    private val pointMapper  = get<PointMapper>()

    override fun fromView(type: CityPointModel) =
            CityPoint(
                    type.name,
                    type.point?.let { pointMapper.fromView(it) },
                    type.placeId
            )

    override fun toView(type: CityPoint) =
            CityPointModel(
                    type.name,
                    type.point?.let { pointMapper.toView(it) },
                    type.placeId
            )
}
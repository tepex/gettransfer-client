package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CityPointEntity
import com.kg.gettransfer.data.model.DestDurationEntity
import com.kg.gettransfer.data.model.DestEntity
import com.kg.gettransfer.data.model.DestPointEntity

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Dest
import com.kg.gettransfer.domain.model.DestDuration
import com.kg.gettransfer.domain.model.DestPoint

import org.koin.standalone.get

open class DestMapper : Mapper<DestEntity<CityPointEntity, Int>, Dest<CityPoint, Int>> {
    private val cityPointMapper = get<CityPointMapper>()

    override fun fromEntity(type: DestEntity<CityPointEntity, Int>): Dest<CityPoint, Int> { throw UnsupportedOperationException() }

    override fun toEntity(type: Dest<CityPoint, Int>): DestEntity<CityPointEntity, Int> = when (type) {
        is DestPoint    -> DestPointEntity(cityPointMapper.toEntity(type.to))
        is DestDuration -> DestDurationEntity(type.duration)
    }
}

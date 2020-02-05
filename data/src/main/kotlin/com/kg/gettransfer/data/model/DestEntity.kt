package com.kg.gettransfer.data.model

import com.kg.gettransfer.core.data.CityPointEntity
import com.kg.gettransfer.core.data.map
import com.kg.gettransfer.core.domain.CityPoint

import com.kg.gettransfer.domain.model.Dest

sealed class DestEntity<CityPointEntity, Int>

class DestPointEntity<CityPointEntity, Int>(val to: CityPointEntity) : DestEntity<CityPointEntity, Int>()

class DestDurationEntity<CityPointEntity, Int>(
    val duration: Int,
    val to: CityPointEntity?
) : DestEntity<CityPointEntity, Int>()

fun Dest<CityPoint, Int>.map(): DestEntity<CityPointEntity, Int> = when (this) {
    is Dest.Point    -> DestPointEntity(to.map())
    is Dest.Duration -> DestDurationEntity(duration, to?.map())
}

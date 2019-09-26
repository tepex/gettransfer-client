package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Dest

sealed class DestEntity<CityPointEntity, Int>

class DestPointEntity<CityPointEntity, Int>(val to: CityPointEntity) : DestEntity<CityPointEntity, Int>()
class DestDurationEntity<CityPointEntity, Int>(val duration: Int) : DestEntity<CityPointEntity, Int>()

fun Dest<CityPoint, Int>.map(): DestEntity<CityPointEntity, Int> = when (this) {
    is Dest.Point    -> DestPointEntity(to.map())
    is Dest.Duration -> DestDurationEntity(duration)
}

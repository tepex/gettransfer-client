package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Dest
import com.kg.gettransfer.domain.model.DestPoint
import com.kg.gettransfer.domain.model.DestDuration

sealed class DestEntity<CityPointEntity, Int>

class DestPointEntity<CityPointEntity, Int>(val to: CityPointEntity): DestEntity<CityPointEntity, Int>()
class DestDurationEntity<CityPointEntity, Int>(val duration: Int): DestEntity<CityPointEntity, Int>()

fun Dest<CityPoint, Int>.map(): DestEntity<CityPointEntity, Int> = when (this) {
    is DestPoint    -> DestPointEntity(to.map())
    is DestDuration -> DestDurationEntity(duration)
}

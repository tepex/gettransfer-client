package com.kg.gettransfer.data.model

sealed class DestEntity<CityPointEntity, Int>

class DestPointEntity<CityPointEntity, Int>(val to: CityPointEntity): DestEntity<CityPointEntity, Int>()
class DestDurationEntity<CityPointEntity, Int>(val duration: Int): DestEntity<CityPointEntity, Int>()

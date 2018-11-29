package com.kg.gettransfer.domain.model

sealed class Dest<CityPoint, Int>

class DestPoint<CityPoint, Int>(val to: CityPoint): Dest<CityPoint, Int>()
class DestDuration<CityPoint, Int>(val duration: Int): Dest<CityPoint, Int>()

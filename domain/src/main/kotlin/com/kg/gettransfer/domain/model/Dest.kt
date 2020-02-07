package com.kg.gettransfer.domain.model

import com.kg.gettransfer.core.domain.CityPoint

sealed class Dest<CityPoint, Int> {

    class Point(val to: CityPoint) : Dest<CityPoint, Int>()
    class Duration(val duration: Int, val to: CityPoint?) : Dest<CityPoint, Int>()
}

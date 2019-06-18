package com.kg.gettransfer.domain.model

data class CityPoint(
    val name: String,
    val point: Point,
    val placeId: String
) {

    companion object {
        val EMPTY = CityPoint("", Point.EMPTY, "")
    }
}

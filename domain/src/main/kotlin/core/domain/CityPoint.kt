package com.kg.gettransfer.core.domain

data class CityPoint(
    val name: String,
    val point: Point?,
    val placeId: String?
) {

    companion object {
        val EMPTY = CityPoint("", null, null)
    }
}

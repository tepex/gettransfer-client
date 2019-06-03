package com.kg.gettransfer.domain.model

data class Point(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {

    override fun toString() = "($latitude,$longitude)"
    companion object {
        val EMPTY_POINT = Point(0.0, 0.0)
    }
}

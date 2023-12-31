package com.kg.gettransfer.core.domain

data class Point(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {

    override fun toString() = "($latitude,$longitude)"

    companion object {
        val EMPTY = Point(Double.NaN, Double.NaN)
    }
}

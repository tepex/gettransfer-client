package com.kg.gettransfer.domain.model

data class Point(
    val latitude: Double,
    val longitude: Double
) {

    override fun toString() = "($latitude,$longitude)"
    
    companion object {
        val EMPTY = Point(Double.NaN, Double.NaN)
    }
}

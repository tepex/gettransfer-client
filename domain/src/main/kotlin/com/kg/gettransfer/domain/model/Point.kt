package com.kg.gettransfer.domain.model

data class Point(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {

    override fun toString() = "($latitude,$longitude)"
}

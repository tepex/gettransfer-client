package com.kg.gettransfer.domain.model

import java.io.Serializable

data class Point(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
): Serializable {

    override fun toString() = "($latitude,$longitude)"
}

package com.kg.gettransfer.domain.model

import java.io.Serializable

data class CityPoint(
    val name: String?,
    var point: Point?,
    val placeId: String?
): Serializable

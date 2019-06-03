package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GTAddressEntity(
    val lat: Double?,
    val lon: Double?,
    val address: String,
    val placeTypes: List<String>?,
    val primary: String?,
    val secondary: String?
)

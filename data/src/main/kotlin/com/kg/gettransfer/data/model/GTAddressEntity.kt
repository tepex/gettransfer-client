package com.kg.gettransfer.data.model

data class GTAddressEntity(val lat: Double,
                           val lon: Double,
                           val address: String,
                           val placeTypes: List<Int>?,
                           val primary: String?,
                           val secondary: String?)
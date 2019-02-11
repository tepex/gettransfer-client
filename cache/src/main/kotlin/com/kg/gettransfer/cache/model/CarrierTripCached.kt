package com.kg.gettransfer.cache.model

data class CarrierTripCached(
        val base: CarrierTripBaseCached,
        val more: CarrierTripMoreCached
)
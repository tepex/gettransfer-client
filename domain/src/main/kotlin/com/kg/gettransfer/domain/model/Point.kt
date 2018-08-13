package com.kg.gettransfer.domain.model

class Point(val latitude: Double = 0.0, val longitude: Double = 0.0)

class LocationResult(val point: Point? = null, val error: Throwable? = null)

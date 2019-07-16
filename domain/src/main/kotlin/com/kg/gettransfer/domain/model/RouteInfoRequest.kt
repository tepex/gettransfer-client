package com.kg.gettransfer.domain.model

import java.util.Date

data class RouteInfoRequest(
    val from: Point,
    val to: Point,
    val withPrices: Boolean,
    val returnWay: Boolean,
    val currency: String,
    val dateTime: Date?
)

data class RouteInfoHourlyRequest(
    val from: Point,
    val hourlyDuration: Int,
    val currency: String,
    val dateTime: Date?
)

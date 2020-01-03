package com.kg.gettransfer.domain.model

import java.util.Date

data class RouteInfoRequest(
    val from: Point,
    val to: Point?,
    val hourlyDuration: Int?,
    val withPrices: Boolean,
    val returnWay: Boolean,
    val currency: String,
    val dateTo: Date?,
    val dateReturn: Date?
)

package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.RouteInfoHourlyRequest

import java.text.DateFormat
import java.util.Date

data class RouteInfoRequestEntity(
    val from: String,
    val to: String,
    val withPrices: Boolean,
    val returnWay: Boolean,
    val currency: String,
    val dateTime: String?
)

data class RouteInfoHourlyRequestEntity(
    val from: String,
    val hourlyDuration: Int,
    val currency: String,
    val dateTime: String?
)

fun RouteInfoRequest.map(dateFormat: DateFormat) =
    RouteInfoRequestEntity(
        from.toString(),
        to.toString(),
        withPrices,
        returnWay,
        currency,
        dateTime?.let { dateFormat.format(it) }
    )

fun RouteInfoHourlyRequest.map(dateFormat: DateFormat) =
    RouteInfoHourlyRequestEntity(
        from.toString(),
        hourlyDuration,
        currency,
        dateTime?.let { dateFormat.format(it) }
    )

package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.RouteInfoRequest
import java.text.DateFormat

data class RouteInfoRequestEntity(
    val from: String,
    val to: String?,
    val hourlyDuration: Int?,
    val withPrices: Boolean,
    val returnWay: Boolean,
    val currency: String,
    val dateTime: String?
)

fun RouteInfoRequest.map(dateFormat: DateFormat) =
    RouteInfoRequestEntity(
        from.toString(),
        to?.toString(),
        hourlyDuration,
        withPrices,
        returnWay,
        currency,
        dateTime?.let { dateFormat.format(it) }
    )

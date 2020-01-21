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
    val dateTo: String?,
    val dateReturn: String?
)

fun RouteInfoRequest.map(dateFormat: DateFormat) =
    RouteInfoRequestEntity(
        from.toString(),
        to?.toString(),
        hourlyDuration,
        withPrices,
        returnWay,
        currency,
        dateTo?.let { dateFormat.format(it) },
        dateReturn?.let { dateFormat.format(it) }
    )

package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.core.domain.Point

data class RouteModel(
    val from: String,
    val to: String?,
    val fromPoint: Point,
    val toPoint: Point,
    val dateTime: String,
    val distance: Int?,
    val isRoundTrip: Boolean,
    val polyLines: List<String>?
)

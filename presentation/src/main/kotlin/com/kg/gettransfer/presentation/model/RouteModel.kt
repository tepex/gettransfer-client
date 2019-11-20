package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.Point

data class RouteModel(
    var distance: Int?,
    val polyLines: List<String>?,
    val from: String,
    val to: String?,
    val fromPoint: Point,
    val toPoint: Point,
    var dateTime: String
)

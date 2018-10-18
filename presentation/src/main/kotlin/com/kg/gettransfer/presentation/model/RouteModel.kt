package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Point

class RouteModel(val distance: Int?,
                 val distanceUnit: DistanceUnit,
                 val polyLines: List<String>?,
                 val from: String,
                 val to: String,
                 val fromPoint: Point,
                 val toPoint: Point,
                 val dateTime: String)

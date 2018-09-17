package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.DistanceUnit

class RouteModel(val distance: Int?,
                 val distanceUnit: DistanceUnit,
                 val polyLines: List<String>,
                 val from: String,
                 val to: String)

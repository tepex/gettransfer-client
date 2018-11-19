package com.kg.gettransfer.presentation.view

import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

interface RouteView {
    fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean = false)
}

package com.kg.gettransfer.presentation.view

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

interface RouteView {
    fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean = false)
    fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate, isDateChanged: Boolean = false)
    fun centerRoute(cameraUpdate: CameraUpdate)
    fun setMapBottomPadding()
}

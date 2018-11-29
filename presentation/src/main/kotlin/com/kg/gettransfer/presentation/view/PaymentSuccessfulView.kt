package com.kg.gettransfer.presentation.view

import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

interface PaymentSuccessfulView: BaseView, CommunicateView {
    //fun call(number: String?)
    fun setRoute(polyline: PolylineModel)
    fun setRemainTime(time: String?)
}
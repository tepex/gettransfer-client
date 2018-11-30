package com.kg.gettransfer.presentation.view

import com.kg.gettransfer.presentation.model.PolylineModel

interface PaymentSuccessfulView: BaseView, CommunicateView {
    fun setRoute(polyline: PolylineModel)
    fun setRemainTime(time: String?)
}
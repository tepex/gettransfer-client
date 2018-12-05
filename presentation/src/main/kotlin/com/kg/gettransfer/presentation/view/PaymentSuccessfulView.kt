package com.kg.gettransfer.presentation.view

import com.kg.gettransfer.presentation.model.PolylineModel

interface PaymentSuccessfulView: BaseView {
    fun setRoute(polyline: PolylineModel)
    fun setRemainTime(days: Int, hours: Int, minutes: Int)
}
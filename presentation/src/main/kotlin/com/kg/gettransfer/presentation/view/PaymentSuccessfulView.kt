package com.kg.gettransfer.presentation.view

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.presentation.model.PolylineModel

interface PaymentSuccessfulView : BaseView {
    fun setRoute(polyline: PolylineModel)
    fun setRemainTime(days: Int, hours: Int, minutes: Int)
    fun setPinHourlyTransfer(point: LatLng, cameraUpdate: CameraUpdate)
    fun initCallButton()
    fun initChatButton()

    companion object {
        val EXTRA_TRANSFER_ID = "${PaymentSuccessfulView::class.java.name}.transferId"
        val EXTRA_OFFER_ID    = "${PaymentSuccessfulView::class.java.name}.offerId"
    }
}

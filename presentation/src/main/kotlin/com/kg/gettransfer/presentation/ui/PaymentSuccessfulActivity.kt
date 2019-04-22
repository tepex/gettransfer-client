package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isGone
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.presenter.PaymentSuccessfulPresenter
import com.kg.gettransfer.presentation.view.PaymentSuccessfulView
import kotlinx.android.synthetic.main.activity_payment_successful.*
import kotlinx.android.synthetic.main.dialog_payment_successful.*

import kotlinx.android.synthetic.main.dialog_payment_successful.view.*

class PaymentSuccessfulActivity : BaseGoogleMapActivity(), PaymentSuccessfulView {

    @InjectPresenter
    internal lateinit var presenter: PaymentSuccessfulPresenter

    private lateinit var bsPayment: BottomSheetBehavior<View>

    override fun getPresenter(): PaymentSuccessfulPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_successful)
        presenter.offerId = intent.getLongExtra(OFFER_ID, 0L)
        presenter.transferId = intent.getLongExtra(TRANSFER_ID, 0L)
        showPaymentDialog(savedInstanceState)
    }

    private fun showPaymentDialog(savedInstanceState: Bundle?) {
        bsPayment = BottomSheetBehavior.from(sheetSuccessPayment)
        bsPayment.state = BottomSheetBehavior.STATE_EXPANDED
        bsPayment.setBottomSheetCallback(bsCallback)

        sheetSuccessPayment.layoutParams.height = getScreenSide(true)

        _mapView = mapViewRoute
        initMapView(savedInstanceState)
        presenter.setMapRoute()

        tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER).plus(" ${presenter.transferId}")
        tvDetails.setOnClickListener { presenter.onDetailsClick() }

        if (presenter.offerId == 0L) {
            tvBookNowSuccess.isVisible = true
            tvSupport.text = getString(R.string.LNG_OFFERS_SUPPORT)
        }
        ivClose.setOnClickListener { finish() }
        btnSupport.setOnClickListener { presenter.sendEmail(null, presenter.transferId) }
    }

    private val bsCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(p0: View, p1: Float) {

        }

        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                bsPayment.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        gm.uiSettings.isScrollGesturesEnabled = false
        gm.uiSettings.isZoomGesturesEnabled = false
    }

    override fun setRoute(polyline: PolylineModel) = setPolylineWithoutInfo(polyline)

    override fun setRemainTime(days: Int, hours: Int, minutes: Int) {
        val time = "$days${getString(R.string.LNG_D)}:$hours${getString(R.string.LNG_H)}:$minutes${getString(R.string.LNG_M)}"
        tvRemainTime.text = time
    }

    companion object {
        const val TRANSFER_ID = "transferId"
        const val OFFER_ID = "offerId"
    }

    override fun setPinHourlyTransfer(point: LatLng, cameraUpdate: CameraUpdate) {
        processGoogleMap(false) { setPinForHourlyWithoutInfo(point, cameraUpdate) }
    }

    override fun initCallButton() {
        btnCall.isVisible = true
        tvCall.isVisible = true
        btnCall.setOnClickListener { presenter.onCallClick() }
    }
}

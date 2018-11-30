package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter

import com.google.android.gms.maps.GoogleMap

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.presenter.PaymentSuccessfulPresenter
import com.kg.gettransfer.presentation.view.PaymentSuccessfulView
import com.kg.gettransfer.utilities.CommunicateMethods

import kotlinx.android.synthetic.main.dialog_payment_successful.view.*

import org.jetbrains.anko.toast
import java.io.File

class PaymentSuccessfulActivity: BaseGoogleMapActivity(), PaymentSuccessfulView {

    companion object {
        const val TRANSFER_ID = "transferId"
        const val OFFER_ID = "offerId"
    }

    @InjectPresenter
    internal lateinit var presenter: PaymentSuccessfulPresenter

    private lateinit var dialogView: View

    override fun getPresenter(): PaymentSuccessfulPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_successful)
        presenter.offerId = intent.getLongExtra(OFFER_ID, 0L)
        presenter.transferId = intent.getLongExtra(TRANSFER_ID, 0L)
        showPaymentDialog(savedInstanceState)
    }

    private fun showPaymentDialog(savedInstanceState: Bundle?) {
        dialogView = layoutInflater.inflate(R.layout.dialog_payment_successful, null)
        AlertDialog.Builder(this).apply { setView(dialogView) }.show().setCanceledOnTouchOutside(false)

        _mapView = dialogView.mapViewRoute
        initMapView(savedInstanceState)
        presenter.setMapRoute()

        with(dialogView) {
            tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER).plus(" ${presenter.transferId}")
            tvDetails.setOnClickListener { presenter.onDetailsClick() }
            btnCall.setOnClickListener   { presenter.onCallClick() }
            tvVoucher.setOnClickListener { toast(getString(com.kg.gettransfer.R.string.coming_soon)) }
            btnChat.setOnClickListener   { toast(getString(com.kg.gettransfer.R.string.coming_soon)) }

            tvDone.setOnClickListener     { finish() }
            btnSupport.setOnClickListener { presenter.sendEmail(null) }
        }
    }

    override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        gm.uiSettings.isScrollGesturesEnabled = false
        gm.uiSettings.isZoomGesturesEnabled   = false
    }

    override fun setRoute(polyline: PolylineModel) = setPolylineWithoutInfo(polyline)

    override fun setRemainTime(time: String?) {
        dialogView.tvRemainTime.text = getString(R.string.transfer_remain_time, time)
    }

    override fun callPhone(phoneCarrier: String?) =
            CommunicateMethods.callPhone(this, phoneCarrier)

    override fun sendEmail(emailCarrier: String?, logsFile: File?) =
            CommunicateMethods.sendEmail(this, emailCarrier, logsFile)
}

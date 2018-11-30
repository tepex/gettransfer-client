package com.kg.gettransfer.presentation.ui

import android.content.ActivityNotFoundException
import android.content.Intent

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter

import com.google.android.gms.maps.GoogleMap

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.presenter.PaymentSuccessfulPresenter
import com.kg.gettransfer.presentation.view.PaymentSuccessfulView

import kotlinx.android.synthetic.main.dialog_payment_successful.view.*

import org.jetbrains.anko.makeCall
import org.jetbrains.anko.toast

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
            tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER, presenter.transferId.toString())
            tvDetails.setOnClickListener { presenter.onDetailsClick() }
            btnCall.setOnClickListener   { presenter.onCallClick() }
            tvVoucher.setOnClickListener { toast(getString(com.kg.gettransfer.R.string.coming_soon)) }
            btnChat.setOnClickListener   { toast(getString(com.kg.gettransfer.R.string.coming_soon)) }

            tvDone.setOnClickListener     { finish() }
            btnSupport.setOnClickListener { sendEmail() }
        }
    }

    override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        gm.uiSettings.isScrollGesturesEnabled = false
        gm.uiSettings.isZoomGesturesEnabled   = false
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            type = "message/rfc822"
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_support)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.LNG_EMAIL_SUBJECT))
        }
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
        } catch(e: ActivityNotFoundException) {
            Utils.showShortToast(this, getString(R.string.no_email_apps))
        }
    }

    override fun call(number: String?) {
        if(number.isNullOrEmpty()) this.toast(getString(R.string.driver_not_number))
        else this.makeCall(number!!)
    }

    override fun setRoute(polyline: PolylineModel) = setPolylineWithoutInfo(polyline)

    override fun setRemainTime(time: String?) {
        dialogView.tvRemainTime.text = getString(R.string.transfer_remain_time, time)
    }
}

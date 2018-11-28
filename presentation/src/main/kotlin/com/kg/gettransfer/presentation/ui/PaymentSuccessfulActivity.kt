package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kg.gettransfer.presentation.presenter.PaymentSuccessfulPresenter
import com.kg.gettransfer.presentation.view.PaymentSuccessfulView

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import kotlinx.android.synthetic.main.activity_payment_successful.*
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.toast

class PaymentSuccessfulActivity : BaseGoogleMapActivity(), PaymentSuccessfulView {

    companion object {
        const val TRANSFER_ID = "transferId"
        const val OFFER_ID = "offerId"
    }

    @InjectPresenter
    internal lateinit var presenter: PaymentSuccessfulPresenter

    override fun getPresenter(): PaymentSuccessfulPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.offerId = intent.getLongExtra(OFFER_ID, 0L)
        presenter.transferId = intent.getLongExtra(TRANSFER_ID, 0L)
        showPaymentDialog(savedInstanceState)
    }

    private fun showPaymentDialog(savedInstanceState: Bundle?) {
        val dialogView = layoutInflater.inflate(R.layout.activity_payment_successful, null)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setView(dialogView)
        builder.show()

        _mapView = mapViewRoute
        initMapView(savedInstanceState)
        presenter.setMapRoute()

        tvDone.setOnClickListener { finish() }
        tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER, presenter.transferId)
        tvDetails.setOnClickListener { presenter.onDetailsClick() }
        tvVoucher.setOnClickListener { this.toast(getString(com.kg.gettransfer.R.string.coming_soon)) }
        btnCall.setOnClickListener { presenter.onCallClick() }
        btnChat.setOnClickListener { this.toast(getString(com.kg.gettransfer.R.string.coming_soon)) }
        btnSupport.setOnClickListener { sendEmail() }
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.type = "message/rfc822"
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_support)))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.LNG_EMAIL_SUBJECT))
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
        } catch (ex: android.content.ActivityNotFoundException) {
            Utils.showShortToast(this, getString(R.string.no_email_apps))
        }
    }

    override fun call(number: String?) {
        number?.let { this.makeCall(it) }
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel) = setPolyline(polyline, routeModel)
}
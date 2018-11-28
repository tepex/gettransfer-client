package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kg.gettransfer.presentation.presenter.PaymentSuccessfulPresenter
import com.kg.gettransfer.presentation.view.PaymentSuccessfulView

import com.kg.gettransfer.R
import kotlinx.android.synthetic.main.activity_payment_successful.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.makeCall

class PaymentSuccessfulActivity : BaseGoogleMapActivity(), PaymentSuccessfulView {

    companion object {
        const val TRANSACTION_ID = "transactionId"
        const val OFFER_ID = "offerId"
    }

    @InjectPresenter
    internal lateinit var presenter: PaymentSuccessfulPresenter

    override fun getPresenter(): PaymentSuccessfulPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.offerId = intent.getLongExtra(OFFER_ID, 0L)
        showPaymentDialog()
    }

    private fun showPaymentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_payment_successful, null)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setView(dialogView)
        builder.show()

        tvDone.setOnClickListener { finish() }
        tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER, intent.getStringExtra(TRANSACTION_ID))
        tvDetails.setOnClickListener {  }
        tvVoucher.setOnClickListener {  }
        btnCall.setOnClickListener { presenter.onCallClick() }
        btnChat.setOnClickListener {  }
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
}
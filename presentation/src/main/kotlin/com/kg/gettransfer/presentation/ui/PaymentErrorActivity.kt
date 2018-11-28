package com.kg.gettransfer.presentation.ui


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.PaymentErrorPresenter
import com.kg.gettransfer.presentation.view.PaymentErrorView
import kotlinx.android.synthetic.main.activity_payment_error.*


class PaymentErrorActivity : BaseActivity(), PaymentErrorView {

    companion object {
        const val TRANSACTION_ID = "transactionId"
    }

    @InjectPresenter
    internal lateinit var presenter: PaymentErrorPresenter

    override fun getPresenter(): PaymentErrorPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_error)
        showPaymentDialog()
    }

    private fun showPaymentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_payment_error, null)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setView(dialogView)
        val dialog = builder.show()

        tvClose.setOnClickListener { finish() }
        tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER, intent.getStringExtra(TRANSACTION_ID))
        btnTryAgain.setOnClickListener { dialog.dismiss() }
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
}

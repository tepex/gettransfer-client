package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog

import com.arellomobile.mvp.presenter.InjectPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.PaymentErrorPresenter
import com.kg.gettransfer.presentation.view.PaymentErrorView
import com.kg.gettransfer.utilities.CommunicateMethods

import kotlinx.android.synthetic.main.dialog_payment_error.view.*
import java.io.File

class PaymentErrorActivity: BaseActivity(), PaymentErrorView {
    companion object {
        const val TRANSFER_ID = "transferId"
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
        val dialogView = layoutInflater.inflate(R.layout.dialog_payment_error, null)
        AlertDialog.Builder(this).apply { setView(dialogView) }.show().setCanceledOnTouchOutside(false)

        with(dialogView) {
            tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER).plus(" ${intent.getLongExtra(TRANSFER_ID, 0L)}")
            tvClose.setOnClickListener     { this@PaymentErrorActivity.finish() }
            btnTryAgain.setOnClickListener { this@PaymentErrorActivity.finish() }
            btnSupport.setOnClickListener  { presenter.sendEmail(null) }
        }
    }

    override fun callPhone(phoneCarrier: String?) =
            CommunicateMethods.callPhone(this, phoneCarrier)

    override fun sendEmail(emailCarrier: String?, logsFile: File?) =
            CommunicateMethods.sendEmail(this, emailCarrier, logsFile)
}

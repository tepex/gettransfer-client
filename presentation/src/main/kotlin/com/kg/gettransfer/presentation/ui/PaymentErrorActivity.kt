package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull

import moxy.presenter.InjectPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.presenter.PaymentErrorPresenter
import com.kg.gettransfer.presentation.view.PaymentErrorView

import kotlinx.android.synthetic.main.dialog_payment_error.view.*

class PaymentErrorActivity : BaseActivity(), PaymentErrorView {

    private lateinit var dialogView: View
    private lateinit var dialog: BottomSheetDialog
    private lateinit var bsPayment: BottomSheetBehavior<View>

    @InjectPresenter
    internal lateinit var presenter: PaymentErrorPresenter

    override fun getPresenter(): PaymentErrorPresenter = presenter

    private var transferId: Long? = null
    private var gatewayId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_error)
        transferId = intent.getLongExtra(PaymentErrorView.EXTRA_TRANSFER_ID, 0L)
        gatewayId = intent.getStringExtra(PaymentErrorView.EXTRA_GATEWAY_ID)
        showPaymentDialog()
    }

    @Suppress("UnsafeCast")
    private fun showPaymentDialog() {
        dialogView = layoutInflater.inflate(R.layout.dialog_payment_error, null)

        dialog = BottomSheetDialog(this, R.style.DialogStyle).apply {
            setContentView(dialogView)
            bsPayment = BottomSheetBehavior.from(dialogView.parent as View)
            bsPayment.state = BottomSheetBehavior.STATE_EXPANDED
            bsPayment.addBottomSheetCallback(bsCallback)
            show()
        }
        dialogView.layoutParams.height = getScreenSide(true) - Utils.dpToPxInt(this, DIALOG_HEIGHT)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        with(dialogView) {
            tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER).plus(" $transferId")
            setErrorInfo(dialogView)
            ivClose.setOnClickListener     { this@PaymentErrorActivity.finish() }
            btnTryAgain.setOnClickListener { this@PaymentErrorActivity.finish() }
            btnSupport.setOnClickListener  { presenter.sendEmail(null, transferId) }
        }
    }

    private fun setErrorInfo(dialogView: View) {
        gatewayId?.let { gateway ->
            if (gateway == PaymentRequestModel.GROUND) {
                dialogView.tvPaymentError.text = getString(R.string.LNG_PAYMENT_BALANCE_ERROR)
            }
        }
    }

    private val bsCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(p0: View, p1: Float) { }

        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                bsPayment.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }

    companion object {
        private const val DIALOG_HEIGHT = 108f
    }
}

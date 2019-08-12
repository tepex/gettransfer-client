package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.NonNull
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter

import com.kg.gettransfer.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_error)
        transferId = intent.getLongExtra(TRANSFER_ID, 0L)
        showPaymentDialog()
    }

    private fun showPaymentDialog() {
        dialogView = layoutInflater.inflate(R.layout.dialog_payment_error, null)

        dialog = BottomSheetDialog(this, R.style.DialogStyle).apply {
            setContentView(dialogView)
            bsPayment = BottomSheetBehavior.from(dialogView.parent as View)
            bsPayment.state = BottomSheetBehavior.STATE_EXPANDED
            bsPayment.setBottomSheetCallback(bsCallback)
            show()
        }
        dialogView.layoutParams.height = getScreenSide(true) - Utils.dpToPxInt(this, 108f)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        with(dialogView) {
            tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER).plus(" $transferId")
            ivClose.setOnClickListener     { this@PaymentErrorActivity.finish() }
            btnTryAgain.setOnClickListener { this@PaymentErrorActivity.finish() }
            btnSupport.setOnClickListener  { presenter.sendEmail(null, transferId) }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }

    companion object {
        const val TRANSFER_ID = "transferId"
    }
}

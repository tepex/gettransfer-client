package com.kg.gettransfer.presentation.ui.dialogs

import android.os.Bundle
import android.view.View

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.view.Screens.showSupportScreen

import kotlinx.android.synthetic.main.dialog_payment_error.*
import kotlinx.android.synthetic.main.view_communication_button.view.*

class PaymentErrorDialog : BaseBottomSheetDialogFragment() {

    override val layout: Int = R.layout.dialog_payment_error

    private var transferId: Long? = null
    private var gatewayId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transferId = arguments?.getLong(TRANSFER_ID, 0L)
        gatewayId = arguments?.getString(GATEWAY_ID)
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER).plus(" $transferId")
        setErrorInfo()
        ivClose.setOnClickListener     { dismiss() }
        btnTryAgain.btnImg.setOnClickListener { dismiss() }
        btnSupport.btnImg.setOnClickListener {
            view?.let { setBottomSheetState(it, BottomSheetBehavior.STATE_HIDDEN) }
            fragmentManager?.let { showSupportScreen(it, transferId) }
        }
    }

    private fun setErrorInfo() {
        gatewayId?.let { gateway ->
            if (gateway == PaymentRequestModel.GROUND) {
                tvPaymentError.text = getString(R.string.LNG_PAYMENT_BALANCE_ERROR)
            }
        }
    }

    companion object {
        const val TRANSFER_ID = "transferId"
        const val GATEWAY_ID  = "gatewayId"
        const val TAG  = "paymentError"
    }
}

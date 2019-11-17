package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.GooglePayPaymentProcess
import com.kg.gettransfer.presentation.model.GooglePayPaymentProcessModel

open class GooglePayPaymentProcessMapper : Mapper<GooglePayPaymentProcessModel, GooglePayPaymentProcess> {
    override fun fromView(type: GooglePayPaymentProcessModel) =
        GooglePayPaymentProcess(
            type.googlePayPaymentId,
            type.token
        )

    override fun toView(type: GooglePayPaymentProcess): GooglePayPaymentProcessModel {
        throw UnsupportedOperationException()
    }
}
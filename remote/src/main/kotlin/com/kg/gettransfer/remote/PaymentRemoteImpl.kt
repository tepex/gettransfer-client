package com.kg.gettransfer.remote

import com.kg.gettransfer.data.PaymentRemote

import com.kg.gettransfer.data.model.BraintreeTokenEntity
import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity
import com.kg.gettransfer.data.model.GooglePayPaymentEntity
import com.kg.gettransfer.data.model.GooglePayPaymentProcessEntity

import com.kg.gettransfer.remote.model.BraintreeTokenModel
import com.kg.gettransfer.remote.model.PaymentModel
import com.kg.gettransfer.remote.model.PaymentStatusRequestModel
import com.kg.gettransfer.remote.model.PaymentStatusWrapperModel
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.WrappedGooglePayPaymentModel
import com.kg.gettransfer.remote.model.map

import org.koin.core.get

class PaymentRemoteImpl : PaymentRemote {
    private val core = get<ApiCore>()

    override suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity {
        val response: ResponseModel<PaymentModel> = core.tryTwice { core.api.createNewPayment(paymentRequest.map()) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun createGooglePayPayment(paymentRequest: PaymentRequestEntity): GooglePayPaymentEntity {
        val response: ResponseModel<WrappedGooglePayPaymentModel> = core.tryTwice { core.api.createNewGooglePayPayment(paymentRequest.map()) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.params.map()
    }

    override suspend fun processGooglePayPayment(paymentProcess: GooglePayPaymentProcessEntity): PaymentEntity {
        val response: ResponseModel<PaymentModel> = core.tryTwice { core.api.processGooglePayPayment(paymentProcess.map()) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity {
        val status = if (paymentStatusRequest.success) {
            PaymentStatusRequestModel.STATUS_SUCCESSFUL
        } else {
            PaymentStatusRequestModel.STATUS_FAILED
        }
        val request = paymentStatusRequest.map()
        val response: ResponseModel<PaymentStatusWrapperModel> = core.tryTwice {
            @Suppress("UnsafeCallOnNullableType")
            core.api.changePaymentStatus(status, request.pgOrderId!!, request.withoutRedirect!!)
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.payment.map()
    }

    override suspend fun getBraintreeToken(): BraintreeTokenEntity {
        val responce: ResponseModel<BraintreeTokenModel> = core.tryTwice { core.api.getBraintreeToken() }
        @Suppress("UnsafeCallOnNullableType")
        return responce.data!!.map()
    }

    override suspend fun confirmPaypal(paymentId: Long, nonce: String): PaymentStatusEntity {
        val response: ResponseModel<PaymentStatusWrapperModel> = core.tryTwice {
            core.api.confirmPaypal(paymentId, nonce)
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.payment.map()
    }
}

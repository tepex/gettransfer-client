package com.kg.gettransfer.remote

import com.kg.gettransfer.data.PaymentRemote
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PlatronPaymentEntity
import com.kg.gettransfer.data.model.CheckoutcomPaymentEntity
import com.kg.gettransfer.data.model.BraintreePaymentEntity
import com.kg.gettransfer.data.model.GooglePayPaymentEntity
import com.kg.gettransfer.data.model.PaymentProcessRequestEntity
import com.kg.gettransfer.data.model.PaymentProcessEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity
import com.kg.gettransfer.data.model.BraintreeTokenEntity
import com.kg.gettransfer.data.model.StringTokenEntity
import com.kg.gettransfer.data.model.JsonTokenEntity
import com.kg.gettransfer.data.model.CheckoutcomTokenRequestEntity
import com.kg.gettransfer.data.model.CheckoutcomTokenEntity

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.PlatronPaymentModel
import com.kg.gettransfer.remote.model.CheckoutcomPaymentModel
import com.kg.gettransfer.remote.model.BraintreePaymentModel
import com.kg.gettransfer.remote.model.GooglePayPaymentModel
import com.kg.gettransfer.remote.model.PaymentProcessModel
import com.kg.gettransfer.remote.model.PaymentStatusWrappedModel
import com.kg.gettransfer.remote.model.BraintreeTokenModel
import com.kg.gettransfer.remote.model.CheckoutcomTokenModel
import com.kg.gettransfer.remote.model.map

import org.koin.core.get

class PaymentRemoteImpl : PaymentRemote {
    private val core = get<ApiCore>()

    override suspend fun createPlatronPayment(paymentRequest: PaymentRequestEntity): PlatronPaymentEntity {
        val response: ResponseModel<PlatronPaymentModel> = core.tryTwice {
            core.api.createPlatronPayment(paymentRequest.map())
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun createCheckoutcomPayment(paymentRequest: PaymentRequestEntity): CheckoutcomPaymentEntity {
        val response: ResponseModel<CheckoutcomPaymentModel> = core.tryTwice {
            core.api.createCheckoutcomPayment(paymentRequest.map())
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun getCheckoutcomToken(
        tokenRequest: CheckoutcomTokenRequestEntity,
        url: String,
        key: String
    ): CheckoutcomTokenEntity {
        core.initChickoutcomApi(url, key)
        val response: CheckoutcomTokenModel = core.tryTwice {
            core.checkoutcomApi.getCheckoutcomToken(tokenRequest.map())
        }
        return response.map()
    }

    override suspend fun createBraintreePayment(paymentRequest: PaymentRequestEntity): BraintreePaymentEntity {
        val response: ResponseModel<BraintreePaymentModel> = core.tryTwice {
            core.api.createBraintreePayment(paymentRequest.map())
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun createGooglePayPayment(paymentRequest: PaymentRequestEntity): GooglePayPaymentEntity {
        val response: ResponseModel<GooglePayPaymentModel> = core.tryTwice {
            core.api.createGooglePayPayment(paymentRequest.map())
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun createGroundPayment(paymentRequest: PaymentRequestEntity) {
        val response: ResponseModel<Unit> = core.tryTwice {
            core.api.createGroundPayment(paymentRequest.map())
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!
    }

    override suspend fun processPayment(paymentProcessRequest: PaymentProcessRequestEntity): PaymentProcessEntity {
        val response: ResponseModel<PaymentProcessModel> = core.tryTwice {
            when (val type = paymentProcessRequest.token) {
                is StringTokenEntity -> core.api.processPayment(paymentProcessRequest.map(type.token))
                is JsonTokenEntity   -> core.api.processPayment(paymentProcessRequest.map(type.token))
            }
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity {
        val status = if (paymentStatusRequest.isSuccess) {
            PaymentStatusRequestEntity.STATUS_SUCCESSFUL
        } else {
            PaymentStatusRequestEntity.STATUS_FAILED
        }
        val response: ResponseModel<PaymentStatusWrappedModel> = core.tryTwice {
            core.api.changePaymentStatus(
                status,
                paymentStatusRequest.paymentId,
                paymentStatusRequest.failureDescription,
                true
            )
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
        val response: ResponseModel<PaymentStatusWrappedModel> = core.tryTwice {
            core.api.confirmPaypal(paymentId, nonce)
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.payment.map()
    }
}

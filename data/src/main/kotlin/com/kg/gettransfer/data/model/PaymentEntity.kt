package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Payment
import com.kg.gettransfer.domain.model.PlatronPayment
import com.kg.gettransfer.domain.model.CheckoutcomPayment
import com.kg.gettransfer.domain.model.PaypalPayment
import com.kg.gettransfer.domain.model.BraintreePayment
import com.kg.gettransfer.domain.model.GooglePayPayment
import com.kg.gettransfer.domain.model.Params
import com.kg.gettransfer.domain.model.BraintreeParams
import com.kg.gettransfer.domain.model.GooglePayParams

open class PaymentEntity(
    val type: String
) {

    fun getMappedType() = Payment.Type.values().find { it.name.toLowerCase() == type } ?: Payment.Type.NOTHING

    companion object {
        const val TYPE       = "type"
        const val URL        = "url"
        const val PAYMENT_ID = "payment_id"
        const val PARAMS     = "params"
        const val AMOUNT_FORMATTED = "amount_formatted"
    }
}

class PlatronPaymentEntity(
    type: String,
    val url: String
) : PaymentEntity(type)

class CheckoutcomPaymentEntity(
    type: String,
    val url: String,
    val paymentId: Long,
    val amountFormatted: String
) : PaymentEntity(type)

class PaypalPaymentEntity(
    type: String,
    val params: ParamsEntity
) : PaymentEntity(type)

class BraintreePaymentEntity(
    type: String,
    val params: BraintreeParamsEntity
) : PaymentEntity(type)

class GooglePayPaymentEntity(
    type: String,
    val params: GooglePayParamsEntity
) : PaymentEntity(type)

open class ParamsEntity(
    val amount: Float,
    val currency: String
) {

    companion object {
        const val AMOUNT     = "amount"
        const val CURRENCY   = "currency"
        const val PAYMENT_ID = "payment_id"
        const val GATEWAY    = "gateway"
        const val GATEWAY_MERCHANT_ID = "gateway_merchant_id"
    }
}

class BraintreeParamsEntity(
    amount: Float,
    currency: String,
    val paymentId: Long
) : ParamsEntity(amount, currency)

class GooglePayParamsEntity(
    amount: Float,
    currency: String,
    val paymentId: Long,
    val gateway: String,
    val gatewayMerchantId: String
) : ParamsEntity(amount, currency)

fun PlatronPaymentEntity.map() =
    PlatronPayment(
        getMappedType(),
        url
    )

fun CheckoutcomPaymentEntity.map() =
    CheckoutcomPayment(
        getMappedType(),
        url,
        paymentId,
        amountFormatted
    )

fun PaypalPaymentEntity.map() =
    PaypalPayment(
        getMappedType(),
        params.map()
    )

fun BraintreePaymentEntity.map() =
    BraintreePayment(
        getMappedType(),
        params.map()
    )

fun GooglePayPaymentEntity.map() =
    GooglePayPayment(
        getMappedType(),
        params.map()
    )

fun ParamsEntity.map() =
    Params(
        amount,
        currency
    )

fun BraintreeParamsEntity.map() =
    BraintreeParams(
        amount,
        currency,
        paymentId
    )

fun GooglePayParamsEntity.map() =
    GooglePayParams(
        amount,
        currency,
        paymentId,
        gateway,
        gatewayMerchantId
    )

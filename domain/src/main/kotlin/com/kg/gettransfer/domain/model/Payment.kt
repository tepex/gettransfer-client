package com.kg.gettransfer.domain.model

open class Payment(
    val type: Type
) {

    enum class Type { IFRAME, WIDGET, NOTHING }

    companion object {
        val EMPTY_TYPE = Type.NOTHING
        val EMPTY = Payment(EMPTY_TYPE)
    }
}

class PlatronPayment(
    type: Type,
    val url: String
) : Payment(type) {

    companion object {
        val EMPTY = PlatronPayment(EMPTY_TYPE, "")
    }
}

class CheckoutcomPayment(
    type: Type,
    val url: String,
    val paymentId: Long
) : Payment(type) {

    companion object {
        val EMPTY = CheckoutcomPayment(EMPTY_TYPE, "", 0L)
    }
}

class PaypalPayment(
    type: Type,
    val params: Params
) : Payment(type) {

    companion object {
        val EMPTY = PaypalPayment(EMPTY_TYPE, Params.EMPTY)
    }
}

class BraintreePayment(
    type: Type,
    val params: BraintreeParams
) : Payment(type) {

    companion object {
        val EMPTY = BraintreePayment(EMPTY_TYPE, BraintreeParams.EMPTY)
    }
}

class GooglePayPayment(
    type: Type,
    val params: GooglePayParams
) : Payment(type) {

    companion object {
        val EMPTY = GooglePayPayment(EMPTY_TYPE, GooglePayParams.EMPTY)
    }
}

open class Params(
    val amount: Float,
    val currency: String
) {

    companion object {
        val EMPTY = Params(0F, "")
    }
}

class BraintreeParams(
    amount: Float,
    currency: String,
    val paymentId: Long
) : Params(amount, currency) {

    companion object {
        val EMPTY = BraintreeParams(0F, "", 0L)
    }
}

class GooglePayParams(
    amount: Float,
    currency: String,
    val paymentId: Long,
    val gateway: String,
    val gatewayMerchantId: String
) : Params(amount, currency) {

    companion object {
        val EMPTY = GooglePayParams(0F, "", 0L, "", "")
    }
}

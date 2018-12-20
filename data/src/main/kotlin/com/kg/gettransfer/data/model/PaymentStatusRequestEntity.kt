package com.kg.gettransfer.data.model

data class PaymentStatusRequestEntity(
    val paymentId: Long?,
    val pgOrderId: Long?,
    val withoutRedirect: Boolean?,
    val success: Boolean
) {

    companion object {
        const val PAYMENT_ID       = "payment_id"
        const val PG_ORDER_ID      = "pg_order_id"
        const val WITHOUT_REDIRECT = "without_redirect"
    }
}

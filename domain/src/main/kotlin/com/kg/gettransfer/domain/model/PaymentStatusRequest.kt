package com.kg.gettransfer.domain.model

class PaymentStatusRequest(val paymentId: Long?, val pgOrderId: Long?, val withoutRedirect: Boolean?, val success: Boolean)

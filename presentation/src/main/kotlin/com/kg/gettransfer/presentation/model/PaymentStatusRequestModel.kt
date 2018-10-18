package com.kg.gettransfer.presentation.model

class PaymentStatusRequestModel(val paymentId: Long?,
                                val pgOrderId: Long?,
                                val withoutRedirect: Boolean?,
                                val success: Boolean)

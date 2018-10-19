package com.kg.gettransfer.data.model

data class PaymentStatusRequestEntity(val paymentId: Long?,
                                      val pgOrderId: Long?,
                                      val withoutRedirect: Boolean?,
                                      val success: Boolean)

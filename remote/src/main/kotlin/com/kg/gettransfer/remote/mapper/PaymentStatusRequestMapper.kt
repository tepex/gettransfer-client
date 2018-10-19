package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PaymentStatusRequestEntity

import com.kg.gettransfer.remote.model.PaymentStatusRequestModel

/**
 * Map a [PaymentStatusRequestModel] from an [PaymentStatusRequestEntity] instance when data is moving between this later and the Data layer.
 */
open class PaymentStatusRequestMapper(): EntityMapper<PaymentStatusRequestModel, PaymentStatusRequestEntity> {
    override fun fromRemote(type: PaymentStatusRequestModel): PaymentStatusRequestEntity { throw UnsupportedOperationException() } 
    override fun toRemote(type: PaymentStatusRequestEntity) = PaymentStatusRequestModel(type.paymentId, type.pgOrderId, type.withoutRedirect)
}

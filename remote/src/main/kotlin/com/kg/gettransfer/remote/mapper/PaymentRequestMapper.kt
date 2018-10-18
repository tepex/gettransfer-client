package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PaymentRequestEntity

import com.kg.gettransfer.remote.model.PaymentRequestModel

/**
 * Map a [PaymentRequestModel] from an [PaymentRequestEntity] instance when data is moving between this later and the Data layer.
 */
open class PaymentRequestMapper(): EntityMapper<PaymentRequestModel, PaymentRequestEntity> {
    override fun fromRemote(type: PaymentRequestModel): PaymentRequestEntity { throw UnsupportedOperationException() } 
    override fun toRemote(type: PaymentRequestEntity) = 
        PaymentRequestModel(type.transferId, type.offerId, type.gatewayId, type.percentage)
}

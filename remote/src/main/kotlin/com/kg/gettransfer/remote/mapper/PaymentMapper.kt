package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PaymentEntity

import com.kg.gettransfer.remote.model.PaymentModel

/**
 * Map a [PaymentModel] from an [PaymentEntity] instance when data is moving between this later and the Data layer.
 */
open class PaymentMapper(): EntityMapper<PaymentModel, PaymentEntity> {
    override fun fromRemote(type: PaymentModel) = PaymentEntity(type.type, type.url)
    override fun toRemote(type: PaymentEntity) = PaymentModel(type.type, type.url)
}

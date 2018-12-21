package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PaymentStatusEntity

import com.kg.gettransfer.remote.model.PaymentStatusModel

/**
 * Map a [PaymentStatusModel] from an [PaymentStatusEntity] instance when data is moving between this later and the Data layer.
 */
open class PaymentStatusMapper : EntityMapper<PaymentStatusModel, PaymentStatusEntity> {
    override fun fromRemote(type: PaymentStatusModel) =
        PaymentStatusEntity(
            id = type.id,
            status = type.status
        )

    override fun toRemote(type: PaymentStatusEntity) =
        PaymentStatusModel(
            id = type.id,
            status = type.status
        )
}

package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ParamsEntity
import com.kg.gettransfer.domain.model.Params

class ParamsMapper: Mapper<ParamsEntity, Params> {
    override fun fromEntity(type: ParamsEntity): Params =
            Params(type.amount, type.currency, type.paymentId)

    override fun toEntity(type: Params): ParamsEntity {
        throw UnsupportedOperationException()
    }
}
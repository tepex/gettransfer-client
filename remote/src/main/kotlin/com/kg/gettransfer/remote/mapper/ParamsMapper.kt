package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.ParamsEntity
import com.kg.gettransfer.remote.model.Params
import java.lang.UnsupportedOperationException

open class ParamsMapper: EntityMapper<Params, ParamsEntity> {
    override fun fromRemote(type: Params): ParamsEntity =
            ParamsEntity(type.amount, type.currency, type.paymentId)

    override fun toRemote(type: ParamsEntity): Params =
            throw UnsupportedOperationException()
}
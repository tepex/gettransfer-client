package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.BraintreeTokenEntity
import com.kg.gettransfer.remote.model.BraintreeTokenModel

open class BraintreeTokenMapper: EntityMapper<BraintreeTokenModel, BraintreeTokenEntity> {
    override fun fromRemote(type: BraintreeTokenModel): BraintreeTokenEntity =
            BraintreeTokenEntity(
                    token = type.token,
                    environment = type.environment)

    override fun toRemote(type: BraintreeTokenEntity): BraintreeTokenModel =
            BraintreeTokenModel(
                    token = type.token,
                    environment = type.environment)
}
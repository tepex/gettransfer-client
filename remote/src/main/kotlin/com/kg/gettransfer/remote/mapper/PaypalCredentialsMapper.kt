package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PaypalCredentialsEntity

import com.kg.gettransfer.remote.model.PaypalCredentialsModel

/**
 * Map a [PaypalCredentialsEntity] from a [PaypalCredentialsModel] instance when data is moving between this later and the Data layer.
 */
open class PaypalCredentialsMapper(): EntityMapper<PaypalCredentialsModel, PaypalCredentialsEntity> {
    override fun fromRemote(type: PaypalCredentialsModel) = PaypalCredentialsEntity(type.id, type.env)
    override fun toRemote(type: PaypalCredentialsEntity): PaypalCredentialsModel { throw UnsupportedOperationException() }
}

package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.PaypalCredentialsCached

import com.kg.gettransfer.data.model.PaypalCredentialsEntity

class PaypalCredentialsEntityMapper: EntityMapper<PaypalCredentialsCached, PaypalCredentialsEntity> {
    override fun fromCached(type: PaypalCredentialsCached) = PaypalCredentialsEntity(type.id, type.env)
    override fun toCached(type: PaypalCredentialsEntity) = PaypalCredentialsCached(type.id, type.env)
}

package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.MobileConfigEntity

import com.kg.gettransfer.remote.model.MobileConfig

open class MobileConfigMapper : EntityMapper<MobileConfig, MobileConfigEntity> {
    override fun fromRemote(type: MobileConfig) =
        MobileConfigEntity(
            pushShowDelay       = type.pushShowDelay,
            orderMinimumMinutes = type.orderMinimumMinutes,
            termsUrl            = type.termsOfUseUrl
        )

    override fun toRemote(type: MobileConfigEntity): MobileConfig { throw UnsupportedOperationException() }
}

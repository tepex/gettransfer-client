package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.remote.model.MobileConfig
import java.lang.UnsupportedOperationException

class MobileConfigMapper: EntityMapper<MobileConfig, MobileConfigEntity> {

    override fun fromRemote(type: MobileConfig) =
            MobileConfigEntity(type.pushShowDelay,
                               type.orderMinimumMinutes,
                               type.termsOfUseUrl)

    override fun toRemote(type: MobileConfigEntity): MobileConfig { throw UnsupportedOperationException() }

}
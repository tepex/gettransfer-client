package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.domain.model.MobileConfig
import java.lang.UnsupportedOperationException

class MobileConfigMapper: Mapper<MobileConfigEntity, MobileConfig> {
    override fun fromEntity(type: MobileConfigEntity) =
            MobileConfig(type.pushShowDelay,
                    type.orderMinimumMinutes,
                    type.termsUrl,
                    MobileConfig.FROM_REMOTE)

    override fun toEntity(type: MobileConfig) = throw UnsupportedOperationException()
}
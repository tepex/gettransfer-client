package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.domain.model.MobileConfig

open class MobileConfigMapper : Mapper<MobileConfigEntity, MobileConfig> {
    override fun fromEntity(type: MobileConfigEntity) =
        MobileConfig(
            pushShowDelay       = type.pushShowDelay,
            orderMinimumMinutes = type.orderMinimumMinutes,
            termsUrl            = type.termsUrl,
            isUpdated           = MobileConfig.FROM_REMOTE
        )

    override fun toEntity(type: MobileConfig): MobileConfigEntity { throw UnsupportedOperationException() }
}

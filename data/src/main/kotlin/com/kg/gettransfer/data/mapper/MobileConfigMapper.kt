package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.data.model.BuildsConfigsEntity
import com.kg.gettransfer.domain.model.MobileConfig
import com.kg.gettransfer.domain.model.BuildsConfigs
import org.koin.standalone.get

open class MobileConfigMapper : Mapper<MobileConfigEntity, MobileConfig> {
    private val buildsConfigsMapper = get<BuildsConfigsMapper>()

    override fun fromEntity(type: MobileConfigEntity) =
        MobileConfig(
            pushShowDelay       = type.pushShowDelay,
            orderMinimumMinutes = type.orderMinimumMinutes,
            termsUrl            = type.termsUrl,
            buildsConfigs       = type.buildsConfigs?.mapValues { buildsConfigsMapper.fromEntity(it.value) },
            isUpdated           = MobileConfig.FROM_REMOTE
        )

    override fun toEntity(type: MobileConfig): MobileConfigEntity { throw UnsupportedOperationException() }
}

open class BuildsConfigsMapper : Mapper<BuildsConfigsEntity, BuildsConfigs> {
    override fun fromEntity(type: BuildsConfigsEntity) =
            BuildsConfigs(
                 updateRequired = type.updateRequired
            )

    override fun toEntity(type: BuildsConfigs): BuildsConfigsEntity { throw UnsupportedOperationException() }
}
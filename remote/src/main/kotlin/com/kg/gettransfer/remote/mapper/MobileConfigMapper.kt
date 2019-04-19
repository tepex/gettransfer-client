package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.data.model.BuildsConfigsEntity

import com.kg.gettransfer.remote.model.MobileConfig
import com.kg.gettransfer.remote.model.BuildsConfigs
import org.koin.standalone.get

open class MobileConfigMapper : EntityMapper<MobileConfig, MobileConfigEntity> {
    private val buildsConfigsMapper = get<BuildsConfigsMapper>()

    override fun fromRemote(type: MobileConfig) =
        MobileConfigEntity(
            pushShowDelay       = type.pushShowDelay,
            orderMinimumMinutes = type.orderMinimumMinutes,
            termsUrl            = type.termsOfUseUrl,
            buildsConfigs       = type.buildsConfigs?.mapValues { buildsConfigsMapper.fromRemote(it.value) }
        )

    override fun toRemote(type: MobileConfigEntity): MobileConfig { throw UnsupportedOperationException() }
}

open class BuildsConfigsMapper : EntityMapper<BuildsConfigs, BuildsConfigsEntity> {
    override fun fromRemote(type: BuildsConfigs) =
            BuildsConfigsEntity (
                 updateRequired = type.updateRequired
            )

    override fun toRemote(type: BuildsConfigsEntity): BuildsConfigs { throw UnsupportedOperationException() }
}
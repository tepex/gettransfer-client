package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.data.model.BuildsConfigsEntity

import com.kg.gettransfer.domain.model.MobileConfig
import com.kg.gettransfer.domain.model.BuildsConfigs

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class MobileConfigMapper : KoinComponent {
    private val buildsConfigsMapper = get<BuildsConfigsMapper>()

    fun fromEntity(type: MobileConfigEntity) =
        MobileConfig(
            pushShowDelay       = type.pushShowDelay,
            orderMinimumMinutes = type.orderMinimumMinutes,
            termsUrl            = type.termsUrl,
            smsResendDelaySec   = type.smsResendDelaySec ?: 90, //Default value
            buildsConfigs       = type.buildsConfigs?.mapValues { buildsConfigsMapper.fromEntity(it.value) }
        )
}

open class BuildsConfigsMapper : KoinComponent {
    fun fromEntity(type: BuildsConfigsEntity) = BuildsConfigs(type.updateRequired)
}

package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.BuildsConfigsCachedMap
import com.kg.gettransfer.cache.model.MobileConfigsCached
import com.kg.gettransfer.data.model.MobileConfigEntity
import org.koin.standalone.get

open class MobileConfigsEntityMapper : EntityMapper<MobileConfigsCached, MobileConfigEntity> {
    private val buildsConfigsMapper = get<BuildsConfigsEntityMapper>()

    override fun fromCached(type: MobileConfigsCached) =
            MobileConfigEntity(
                    pushShowDelay = type.pushShowDelay,
                    orderMinimumMinutes = type.orderMinimumMinutes,
                    termsUrl = type.termsUrl,
                    smsResendDelaySec = type.smsResendDelaySec,
                    buildsConfigs = type.buildsConfigs?.map?.mapValues { buildsConfigsMapper.fromCached(it.value) }
            )

    override fun toCached(type: MobileConfigEntity) =
            MobileConfigsCached(
                    pushShowDelay = type.pushShowDelay,
                    orderMinimumMinutes = type.orderMinimumMinutes,
                    termsUrl = type.termsUrl,
                    smsResendDelaySec = type.smsResendDelaySec,
                    buildsConfigs = type.buildsConfigs?.let { configs -> BuildsConfigsCachedMap(configs.mapValues { buildsConfigsMapper.toCached(it.value) }) }
            )
}
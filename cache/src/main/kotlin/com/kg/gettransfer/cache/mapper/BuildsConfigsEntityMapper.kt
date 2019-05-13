package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.BuildsConfigsCached
import com.kg.gettransfer.data.model.BuildsConfigsEntity

open class BuildsConfigsEntityMapper : EntityMapper<BuildsConfigsCached, BuildsConfigsEntity> {
    override fun fromCached(type: BuildsConfigsCached) =
            BuildsConfigsEntity(
                    updateRequired = type.updateRequired
            )

    override fun toCached(type: BuildsConfigsEntity) =
            BuildsConfigsCached(
                    updateRequired = type.updateRequired
            )
}
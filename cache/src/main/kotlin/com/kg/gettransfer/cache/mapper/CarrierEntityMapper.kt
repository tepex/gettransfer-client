package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.CarrierCached
import com.kg.gettransfer.cache.model.LocaleCachedList
import com.kg.gettransfer.cache.model.map

import com.kg.gettransfer.data.model.CarrierEntity

import org.koin.standalone.get

open class CarrierEntityMapper : EntityMapper<CarrierCached, CarrierEntity> {
    private val profileCached = get<ProfileEntityMapper>()
    private val localeMapper = get<LocaleEntityMapper>()

    override fun fromCached(type: CarrierCached) =
            CarrierEntity(
                    id = type.id,
                    profile = type.profile?.let { profileCached.fromCached(it) },
                    approved = type.approved,
                    completedTransfers = type.completedTransfers,
                    languages = type.languages.list.map { localeMapper.fromCached(it)},
                    ratings = type.ratings.map(),
                    canUpdateOffers = type.canUpdateOffers
            )

    override fun toCached(type: CarrierEntity) =
            CarrierCached(
                    id = type.id,
                    profile = type.profile?.let { profileCached.toCached(it) },
                    approved = type.approved,
                    completedTransfers = type.completedTransfers,
                    languages = LocaleCachedList(type.languages.map { localeMapper.toCached(it) }),
                    ratings = type.ratings.map(),
                    canUpdateOffers = type.canUpdateOffers
            )
}
package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Carrier

import com.kg.gettransfer.presentation.model.CarrierModel

import org.koin.standalone.get

open class CarrierMapper : Mapper<CarrierModel, Carrier> {
    private val profileMapper = get<ProfileMapper>()
    private val ratingsMapper = get<RatingsMapper>()
    private val localeMapper  = get<LocaleMapper>()

    override fun toView(type: Carrier) =
        CarrierModel(
            type.profile?.let { profileMapper.toView(it) },
            type.approved,
            type.completedTransfers,
            type.languages.map { localeMapper.toView(it) },
            ratingsMapper.toView(type.ratings),
            type.canUpdateOffers
        )

    override fun fromView(type: CarrierModel): Carrier { throw UnsupportedOperationException() }
}

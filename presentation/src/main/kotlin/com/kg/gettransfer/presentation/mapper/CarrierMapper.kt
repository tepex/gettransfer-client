package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Carrier

import com.kg.gettransfer.presentation.model.CarrierModel
import com.kg.gettransfer.presentation.model.map

import org.koin.standalone.get

open class CarrierMapper : Mapper<CarrierModel, Carrier> {
    private val profileMapper = get<ProfileMapper>()
    private val localeMapper  = get<LocaleMapper>()

    override fun toView(type: Carrier) =
        CarrierModel(
            id                 = type.id,
            profile            = type.profile?.let { profileMapper.toView(it) },
            approved           = type.approved,
            completedTransfers = type.completedTransfers,
            languages          = type.languages.map { localeMapper.toView(it) },
            ratings            = type.ratings.map(),
            canUpdateOffers    = type.canUpdateOffers
        )

    override fun fromView(type: CarrierModel): Carrier { throw UnsupportedOperationException() }
}

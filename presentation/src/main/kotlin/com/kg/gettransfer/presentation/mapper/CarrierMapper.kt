package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Carrier

import com.kg.gettransfer.presentation.model.CarrierModel
import com.kg.gettransfer.presentation.model.map

import org.koin.core.get

open class CarrierMapper : Mapper<CarrierModel, Carrier> {
    private val profileMapper = get<ProfileMapper>()

    override fun toView(type: Carrier) =
        CarrierModel(
            id                 = type.id,
            profile            = type.profile?.let { profileMapper.toView(it) },
            approved           = type.approved,
            completedTransfers = type.completedTransfers,
            languages          = type.languages.map { it.map() },
            ratings            = type.ratings,
            canUpdateOffers    = type.canUpdateOffers
        )

    override fun fromView(type: CarrierModel): Carrier { throw UnsupportedOperationException() }
}

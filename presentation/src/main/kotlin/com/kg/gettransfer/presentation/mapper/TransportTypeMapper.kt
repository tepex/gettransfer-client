package com.kg.gettransfer.presentation.mapper

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.presentation.model.TransportTypePriceModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

open class TransportTypeMapper : Mapper<TransportTypeModel, TransportType> {
    var prices: Map<TransportType.ID, TransportTypePriceModel>? = null

    override fun toView(type: TransportType) =
        TransportTypeModel(
            id = type.id,
            nameId = getNameById(type.id),
            imageId = getImageById(type.id),
            paxMax = type.paxMax,
            luggageMax = type.luggageMax,
            price = prices?.get(type.id),
            description = getDescriptionById(type.id)
        )

    override fun fromView(type: TransportTypeModel): TransportType { throw UnsupportedOperationException() }

    companion object {
        @StringRes
        fun getNameById(id: TransportType.ID): Int {
            val nameId = (if (id == TransportType.ID.LIMOUSINE) TransportType.ID.VIP else id).name
            val nameRes = R.string::class.members.find( { it.name == "LNG_TRANSPORT_$nameId" } )
            return (nameRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_ECONOMY
        }

        @DrawableRes
        fun getImageById(id: TransportType.ID): Int {
            val nameId = if (id == TransportType.ID.LIMOUSINE) TransportType.ID.VIP else id
            val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_$nameId" } )
            return (imageRes?.call() as Int?) ?: R.drawable.ic_transport_type_economy
        }

        @DrawableRes
        fun getEmptyImageById(id: TransportType.ID): Int {
            val nameId = if (id == TransportType.ID.LIMOUSINE) TransportType.ID.VIP else id
            val imageRes = R.drawable::class.members.find( { it.name == "ic_empty_car_$nameId" } )
            return (imageRes?.call() as Int?) ?: R.drawable.ic_empty_car_economy
        }

        @StringRes
        fun getDescriptionById(id: TransportType.ID): Int {
            val nameId = (if (id == TransportType.ID.LIMOUSINE) TransportType.ID.VIP else id).name
            val nameRes = R.string::class.members.find( { it.name == "LNG_TRANSPORT_EXAMPLES_$nameId" } )
            return (nameRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_EXAMPLES_ECONOMY
        }

        @StringRes
        fun getModelsById(id: TransportType.ID): Int {
            val nameId = (if (id == TransportType.ID.LIMOUSINE) TransportType.ID.VIP else id).name
            val modelRes = R.string::class.members.find({it.name == "LNG_TRANSPORT_${nameId}_MODELS"})
            return (modelRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_ECONOMY_MODELS
        }
    }
}

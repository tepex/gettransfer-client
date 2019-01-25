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
            val nameRes = R.string::class.members.find( { it.name == "LNG_TRANSPORT_${id.name}" } )
            return (nameRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_ECONOMY
        }

        @DrawableRes
        fun getImageById(id: TransportType.ID): Int {
            val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_$id" } )
            return (imageRes?.call() as Int?) ?: R.drawable.ic_transport_type_unknown
        }

        @StringRes
        fun getDescriptionById(id: TransportType.ID): Int {
            val nameRes = R.string::class.members.find( { it.name == "LNG_TRANSPORT_EXAMPLES_${id.name}" } )
            return (nameRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_EXAMPLES_ECONOMY
        }

        @StringRes
        fun getModelsById(id: TransportType.ID): Int {
            val modelRes = R.string::class.members.find({it.name == "LNG_TRANSPORT_${id.name}_MODELS"})
            return (modelRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_ECONOMY_MODELS
        }
    }
}

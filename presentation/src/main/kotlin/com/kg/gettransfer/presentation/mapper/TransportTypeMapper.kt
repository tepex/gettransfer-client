package com.kg.gettransfer.presentation.mapper

import android.support.annotation.StringRes

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.presentation.model.TransportPriceModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

open class TransportTypeMapper : Mapper<TransportTypeModel, TransportType> {
    var prices: Map<String, TransportPriceModel>? = null

    override fun toView(type: TransportType): TransportTypeModel {
        val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_${type.id}" } )
        val imageId = (imageRes?.call() as Int?) ?: R.drawable.ic_transport_type_unknown
        return TransportTypeModel(
            type.id,
            getNameById(type.id),
            imageId,
            type.paxMax,
            type.luggageMax,
            prices?.get(type.id)
        )
    }

    override fun fromView(type: TransportTypeModel): TransportType { throw UnsupportedOperationException() }

    companion object {
        @StringRes
        fun getNameById(id: String): Int {
            val nameRes = R.string::class.members.find( { it.name == "LNG_TRANSPORT_${id.toUpperCase()}" } )
            return (nameRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_ECONOMY
        }
    }
}

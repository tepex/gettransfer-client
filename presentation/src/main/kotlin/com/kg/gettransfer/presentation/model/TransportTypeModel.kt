package com.kg.gettransfer.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportType.ID

data class TransportTypeModel(
    val id: ID,
    @StringRes
    val nameId: Int,
    @DrawableRes
    val imageId: Int,
    val paxMax: Int,
    val luggageMax: Int,
    val price: TransportTypePriceModel? = null,
    var checked: Boolean = false,
    @StringRes
    var description: Int? = null
)

fun TransportType.map(prices: Map<ID, TransportTypePriceModel>? = null) =
    TransportTypeModel(
        id,
        id.getNameRes(),
        id.getImageRes(),
        paxMax,
        luggageMax,
        prices?.get(id),
        false,
        id.getDescriptionRes()
    )

@StringRes
fun ID.getNameRes(): Int {
    val nameId = (if (this == ID.LIMOUSINE) ID.VIP else this).name
    val nameRes = R.string::class.members.find( { it.name == "LNG_TRANSPORT_$nameId" } )
    return (nameRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_ECONOMY
}

@DrawableRes
fun ID.getImageRes(): Int {
    val nameId = if (this == ID.LIMOUSINE) ID.VIP else this
    val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_$nameId" } )
    return (imageRes?.call() as Int?) ?: R.drawable.ic_transport_type_economy
}

@DrawableRes
fun ID.getEmptyImageRes(): Int {
    val nameId = if (this == ID.LIMOUSINE) ID.VIP else this
    val imageRes = R.drawable::class.members.find( { it.name == "ic_empty_car_$nameId" } )
    return (imageRes?.call() as Int?) ?: R.drawable.ic_empty_car_economy
}

@StringRes
fun ID.getDescriptionRes(): Int {
    val nameRes = R.string::class.members.find { it.name == "LNG_TRANSPORT_EXAMPLES_${this.name}" }
    return (nameRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_EXAMPLES_ECONOMY
}

@StringRes
fun ID.getModelsRes(): Int {
    val nameId = (if (this == ID.LIMOUSINE) ID.VIP else this).name
    val modelRes = R.string::class.members.find({it.name == "LNG_TRANSPORT_${nameId}_MODELS"})
    return (modelRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_ECONOMY_MODELS
}

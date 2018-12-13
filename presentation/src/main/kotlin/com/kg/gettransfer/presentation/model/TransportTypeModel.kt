package com.kg.gettransfer.presentation.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

data class TransportTypeModel(
    val id: String,
    @StringRes
    val nameId: Int?,
    @DrawableRes
    val imageId: Int?,
    val paxMax: Int,
    val luggageMax: Int,
    val price: TransportPriceModel? = null,
    var checked: Boolean = false,
    @StringRes
    var description: Int? = null
)

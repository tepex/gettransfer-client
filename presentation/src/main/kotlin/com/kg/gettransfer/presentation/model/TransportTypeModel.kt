package com.kg.gettransfer.presentation.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

import com.kg.gettransfer.domain.model.TransportType

data class TransportTypeModel(
    val id: TransportType.ID,
    @StringRes
    val nameId: Int?,
    @DrawableRes
    val imageId: Int?,
    val paxMax: Int,
    val luggageMax: Int,
    val price: TransportTypePriceModel? = null,
    var checked: Boolean = false,
    @StringRes
    var description: Int? = null
)

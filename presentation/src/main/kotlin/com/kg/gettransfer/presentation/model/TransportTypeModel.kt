package com.kg.gettransfer.presentation.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

class TransportTypeModel(val id: String,
                         @StringRes val nameId: Int?,
                         @DrawableRes val imageId: Int?,
                         val paxMax: Int,
                         val luggageMax: Int,
                         val price: TransportPrice?,
                         var checked: Boolean = false,
                         @StringRes var description: Int? = null)

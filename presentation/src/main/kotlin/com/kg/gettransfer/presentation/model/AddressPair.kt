package com.kg.gettransfer.presentation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
 * https://proandroiddev.com/parcelable-in-kotlin-here-comes-parcelize-b998d5a5fcac
 */
@Parcelize
data class AddressPair(val from: String, val to: String): Parcelable

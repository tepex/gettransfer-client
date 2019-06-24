package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.TransportTypePriceEntity

data class TransportTypePriceModel(
    @SerializedName(TransportTypePriceEntity.MIN_FLOAT) @Expose val minFloat: Float,
    @SerializedName(TransportTypePriceEntity.MIN) @Expose val min: String,
    @SerializedName(TransportTypePriceEntity.BOOK_NOW) @Expose val bookNow: String?
)

fun Map.Entry<String, TransportTypePriceModel>.map() =
    TransportTypePriceEntity(value.minFloat, value.min, value.bookNow)

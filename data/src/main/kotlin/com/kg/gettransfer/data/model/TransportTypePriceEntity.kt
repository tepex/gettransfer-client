package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportTypePrice

data class TransportTypePriceEntity(
    val minFloat: Float,
    val min: String,
    val bookNow: String?
) {

    companion object {
        const val MIN_FLOAT = "min_float"
        const val MIN       = "min"
        const val BOOK_NOW  = "bookNow"
    }
}

fun TransportTypePrice.map() = TransportTypePriceEntity(minFloat, min, bookNow?.toString())
fun TransportTypePriceEntity.map() = TransportTypePrice(minFloat, min, bookNow?.map())

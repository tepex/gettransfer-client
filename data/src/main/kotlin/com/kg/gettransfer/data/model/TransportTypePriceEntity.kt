package com.kg.gettransfer.data.model

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

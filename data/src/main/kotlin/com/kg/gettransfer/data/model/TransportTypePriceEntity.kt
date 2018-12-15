package com.kg.gettransfer.data.model

data class TransportTypePriceEntity(
    val transferId: String,
    val minFloat: Float?,
    val min: String?,
    val max: String?
) {

    companion object {
        const val MIN_FLOAT = "min_float"
        const val MIN       = "min"
        const val MAX       = "max"
    }
}

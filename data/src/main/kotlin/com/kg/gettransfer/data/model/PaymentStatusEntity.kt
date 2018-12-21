package com.kg.gettransfer.data.model

data class PaymentStatusEntity(
    val id: Long,
    val status: String
) {

    companion object {
        const val ENTITY_NAME = "payment"
        const val ID          = "id"
        const val STATUS      = "status"
    }
}

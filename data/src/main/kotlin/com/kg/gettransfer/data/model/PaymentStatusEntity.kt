package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.PaymentStatus

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

fun PaymentStatusEntity.map() =
    PaymentStatus(
        id,
        PaymentStatus.Status.values().find { it.name.toLowerCase() == status } ?: PaymentStatus.Status.NOTHING
    )

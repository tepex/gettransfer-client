package com.kg.gettransfer.domain.model

data class PaymentStatus(
    val id: Long,
    val status: String
) {

    val success = status == STATUS_SUCCESS

    companion object {
        const val STATUS_NEW     = "new"
        const val STATUS_PENDING = "pending"
        const val STATUS_SUCCESS = "success"
        const val STATUS_FAILED  = "failed"
    }
}

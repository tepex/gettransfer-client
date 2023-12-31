package com.kg.gettransfer.domain.model

data class PaymentStatus(
    val id: Long,
    val status: Status
) {

    val isSuccess = status == Status.SUCCESS
    val isFailed  = status == Status.FAILED

    enum class Status { NEW, PENDING, SUCCESS, FAILED, NOTHING }

    companion object {
        val EMPTY = PaymentStatus(0, Status.NOTHING)
    }
}

package com.kg.gettransfer.domain.model

data class CheckoutcomToken(
    val token: String?,
    val errorType: String?,
    val errorCodes: List<String>?
) {
    companion object {
        val EMPTY = CheckoutcomToken(null, null, null)
    }
}

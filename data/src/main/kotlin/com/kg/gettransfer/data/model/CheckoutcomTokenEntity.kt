package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.CheckoutcomToken

data class CheckoutcomTokenEntity(
    val token: String?,
    val errorType: String?,
    val errorCodes: List<String>?
) {
    companion object {
        const val TOKEN       = "token"
        const val ERROR_TYPE  = "error_type"
        const val ERROR_CODES = "error_codes"
    }
}

fun CheckoutcomTokenEntity.map() =
    CheckoutcomToken(
        token,
        errorType,
        errorCodes
    )

package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.BraintreeToken
import kotlinx.serialization.SerialName

data class BraintreeTokenEntity(
    @SerialName(TOKEN)       val token: String,
    @SerialName(ENVIRONMENT) val environment: String
) {

    companion object {
        const val TOKEN = "token"
        const val ENVIRONMENT = "environment"
    }
}

fun BraintreeTokenEntity.map() = BraintreeToken(token, environment)

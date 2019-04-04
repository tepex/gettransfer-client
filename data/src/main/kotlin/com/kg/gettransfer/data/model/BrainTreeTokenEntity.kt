package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName

data class BraintreeTokenEntity(
        @SerialName(TOKEN)       val token: String,
        @SerialName(ENVIRONMENT) val environment: String) {

    companion object {
        const val TOKEN = "token"
        const val ENVIRONMENT = "environment"
    }
}

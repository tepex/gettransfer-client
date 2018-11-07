package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MoneyEntity(@SerialName(DEFAULT) val default: String,
                       @SerialName(PREFERRED) val preferred: String?) {
    companion object {
        const val DEFAULT   = "default"
        const val PREFERRED = "preferred"
    }
}

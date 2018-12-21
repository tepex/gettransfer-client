package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName

@Serializable
data class MoneyEntity(
    @SerialName(DEFAULT) val def: String,
    @SerialName(PREFERRED) @Optional val preferred: String? = null
) {

    companion object {
        const val DEFAULT   = "default"
        const val PREFERRED = "preferred"
    }
}

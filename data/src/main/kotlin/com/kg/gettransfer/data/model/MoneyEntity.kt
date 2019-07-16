package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Money
import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

fun Money.map() = MoneyEntity(def, preferred)
fun MoneyEntity.map() = Money(def, preferred)

package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ProfileEntity(
    @SerialName(ID) val id: Long?,
    @SerialName(FULL_NAME) val fullName: String? = null,
    @SerialName(EMAIL) val email: String? = null,
    @SerialName(PHONE) val phone: String? = null
) {

    companion object {
        const val ID        = "id"
        const val FULL_NAME = "full_name"
        const val EMAIL     = "email"
        const val PHONE     = "phone"
        const val TITLE     = "title"
    }
}

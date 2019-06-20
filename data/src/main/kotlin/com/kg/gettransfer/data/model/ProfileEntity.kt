package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Profile

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ProfileEntity(
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

fun Profile.map() = ProfileEntity(fullName, email, phone)

fun ProfileEntity.map() =
    if (fullName == null && email == null && phone == null) {
        Profile.EMPTY
    } else {
        Profile(fullName ?: "", email ?: "", phone ?: "")
    }

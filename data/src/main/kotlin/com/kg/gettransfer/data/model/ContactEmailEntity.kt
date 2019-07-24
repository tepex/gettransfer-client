package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.ContactEmail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactEmailEntity(
    @SerialName(ID) val id: String,
    @SerialName(EMAIL) val email: String
) {

    companion object {
        const val ID = "id"
        const val EMAIL = "email"
    }

    fun stringIdToEnum(id: String): ContactEmail.EmailId =
        try {
            enumValueOf(id.toUpperCase())
        } catch (e: IllegalArgumentException) {
            ContactEmail.EmailId.UNKNOWN
        }
}

fun ContactEmail.map() = ContactEmailEntity(id.toString(), email)

fun ContactEmailEntity.map() = ContactEmail(stringIdToEnum(id), email)

fun ContactEmail.EmailId.map() = toString()

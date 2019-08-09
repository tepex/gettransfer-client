package com.kg.gettransfer.sys.data

import com.kg.gettransfer.sys.domain.ContactEmail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactEmailEntity(
    @SerialName(ID) val id: String,
    @SerialName(EMAIL) val email: String
) {

    fun stringIdToEnum(id: String): ContactEmail.Id =
        try {
            enumValueOf(id.toUpperCase())
        } catch (e: IllegalArgumentException) {
            ContactEmail.Id.UNKNOWN
        }

    companion object {
        const val ID = "id"
        const val EMAIL = "email"
    }
}

fun ContactEmail.map() = ContactEmailEntity(id.toString(), email)

fun ContactEmailEntity.map() = ContactEmail(stringIdToEnum(id), email)

fun ContactEmail.Id.map() = toString()

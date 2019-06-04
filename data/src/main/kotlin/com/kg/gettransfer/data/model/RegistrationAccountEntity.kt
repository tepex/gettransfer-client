package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity for the registration user
 *
 * @author П. Густокашин (Diwixis)
 */

@Serializable
data class RegistrationAccountEntity(
    @SerialName(FULL_NAME) val fullName: String? = null,
    @SerialName(EMAIL) val email: String? = null,
    @SerialName(PHONE) val phone: String? = null,
    @SerialName(TERMS_ACCEPTED) val termsAccepted: Boolean = true
) {

    companion object {
        const val EMAIL = "email"
        const val PHONE = "phone"
        const val FULL_NAME = "full_name"
        const val TERMS_ACCEPTED = "terms_accepted"
    }
}
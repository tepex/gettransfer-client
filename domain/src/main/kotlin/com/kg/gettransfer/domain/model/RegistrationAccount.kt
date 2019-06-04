package com.kg.gettransfer.domain.model

/**
 * Entity for the registration user
 *
 * @author П. Густокашин (Diwixis)
 */
data class RegistrationAccount(
    val email: String,
    val phone: String,
    val termsAccepted: Boolean = true,
    val fullName: String? = null
)
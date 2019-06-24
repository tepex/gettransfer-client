package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity

/**
 *
 *
 * @author П. Густокашин (Diwixis)
 */
data class RegistrationAccountEntityWrapper(
    @SerializedName(AccountEntity.ENTITY_NAME) @Expose val account: RegistrationAccountModel
)

data class RegistrationAccountModel(
    @SerializedName(FULL_NAME) val fullName: String? = null,
    @SerializedName(EMAIL) val email: String? = null,
    @SerializedName(PHONE) val phone: String? = null,
    @SerializedName(TERMS_ACCEPTED) val termsAccepted: Boolean = true
) {

    companion object {
        const val EMAIL = "email"
        const val PHONE = "phone"
        const val FULL_NAME = "full_name"
        const val TERMS_ACCEPTED = "terms_accepted"
    }
}

fun RegistrationAccountEntity.map() = RegistrationAccountModel(fullName, email, phone, termsAccepted)

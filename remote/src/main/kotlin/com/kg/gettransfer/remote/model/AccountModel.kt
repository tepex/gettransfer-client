package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Currency
import java.util.Locale

const val ACCOUNT                = "account"

const val ACCOUNT_PASSWORD       = "password"
const val ACCOUNT_LOCALE         = "locale"
const val ACCOUNT_CURRENCY       = "currency"
const val ACCOUNT_DISTANCE_UNIT  = "distance_unit"
const val ACCOUNT_GROUPS         = "groups"
const val ACCOUNT_TERMS_ACCEPTED = "terms_accepted"
const val ACCOUNT_CARRIER_ID     = "carrier_id"

class AccountModelWrapper(@SerializedName(ACCOUNT) @Expose val account: AccountModel)

class AccountModel(fullName: String?, email: String?, phone: String?,
                   @SerializedName(ACCOUNT_LOCALE) @Expose val locale: String? = null,
                   @SerializedName(ACCOUNT_CURRENCY) @Expose val currency: String? = null,
                   @SerializedName(ACCOUNT_DISTANCE_UNIT) @Expose val distanceUnit: String? = null,
                   @SerializedName(ACCOUNT_GROUPS) @Expose val groups: Array<String>? = null,
                   @SerializedName(ACCOUNT_TERMS_ACCEPTED) @Expose val termsAccepted: Boolean = true,
                   @SerializedName(ACCOUNT_CARRIER_ID) @Expose val carrierId: Long? = null): ProfileModel(fullName, email, phone)

package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Currency
import java.util.Locale

const val ACCOUNT                = "account"

const val ACCOUNT_EMAIL          = "email"
const val ACCOUNT_PASSWORD       = "password"
const val ACCOUNT_PHONE          = "phone"
const val ACCOUNT_LOCALE         = "locale"
const val ACCOUNT_CURRENCY       = "currency"
const val ACCOUNT_DISTANCE_UNIT  = "distance_unit"
const val ACCOUNT_FULL_NAME      = "full_name"
const val ACCOUNT_GROUPS         = "groups"
const val ACCOUNT_TERMS_ACCEPTED = "terms_accepted"

class AccountWrapperModel(@SerializedName(ACCOUNT) @Expose var account: AccountModel)

class AccountModel(@SerializedName(ACCOUNT_EMAIL) @Expose var email: String? = null,
                   @SerializedName(ACCOUNT_PHONE) @Expose var phone: String? = null,
                   @SerializedName(ACCOUNT_LOCALE) @Expose var locale: String? = null,
                   @SerializedName(ACCOUNT_CURRENCY) @Expose var currency: String? = null,
                   @SerializedName(ACCOUNT_DISTANCE_UNIT) @Expose var distanceUnit: String? = null,
                   @SerializedName(ACCOUNT_FULL_NAME) @Expose var fullName: String? = null,
                   @SerializedName(ACCOUNT_GROUPS) @Expose var groups: Array<String>? = null,
                   @SerializedName(ACCOUNT_TERMS_ACCEPTED) @Expose var termsAccepted: Boolean = true)

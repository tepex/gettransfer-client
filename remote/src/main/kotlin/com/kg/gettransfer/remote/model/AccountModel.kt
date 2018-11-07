package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.AccountEntity

class AccountModelWrapper(@SerializedName(AccountEntity.ENTITY_NAME) @Expose val account: AccountModel)

class AccountModel(fullName: String?, email: String?, phone: String?,
                   @SerializedName(AccountEntity.LOCALE) @Expose val locale: String? = null,
                   @SerializedName(AccountEntity.CURRENCY) @Expose val currency: String? = null,
                   @SerializedName(AccountEntity.DISTANCE_UNIT) @Expose val distanceUnit: String? = null,
                   @SerializedName(AccountEntity.GROUPS) @Expose val groups: List<String>? = null,
                   @SerializedName(AccountEntity.TERMS_ACCEPTED) @Expose val termsAccepted: Boolean = true,
                   @SerializedName(AccountEntity.CARRIER_ID) @Expose val carrierId: Long? = null): ProfileModel(fullName, email, phone)

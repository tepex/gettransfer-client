package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity

data class AccountModelWrapper(@SerializedName(AccountEntity.ENTITY_NAME) @Expose val account: AccountModel)

// переделать в делегата
// https://medium.com/@nwillc/kotlin-data-class-inheritance-by-delegation-2ad3fe6f9bd7
class AccountModel(
    fullName: String?,
    email: String?,
    phone: String?,
    @SerializedName(AccountEntity.LOCALE) @Expose val locale: String? = null,
    @SerializedName(AccountEntity.CURRENCY) @Expose val currency: String? = null,
    @SerializedName(AccountEntity.DISTANCE_UNIT) @Expose val distanceUnit: String? = null,
    @SerializedName(AccountEntity.GROUPS) @Expose val groups: List<String>? = null,
    @SerializedName(UserEntity.TERMS_ACCEPTED) @Expose val termsAccepted: Boolean = true,
    @SerializedName(AccountEntity.CARRIER_ID) @Expose val carrierId: Long? = null,
    @SerializedName(AccountEntity.PASSWORD) @Expose val password: String? = null,
    @SerializedName(AccountEntity.PASSWORD_CONFIRMATION) @Expose val repeatedPassword: String? = null,
    @SerializedName(AccountEntity.PARTNER) @Expose val partner: PartnerModel? = null
) : ProfileModel(fullName, email, phone) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        @Suppress("UnsafeCast")
        other as AccountModel
        return email == other.email
    }

    override fun hashCode() = email?.hashCode() ?: 0
}

fun AccountModel.map() =
    AccountEntity(
        UserEntity(ProfileEntity(fullName, email, phone), termsAccepted),
        locale,
        currency,
        distanceUnit,
        groups,
        carrierId,
        password,
        repeatedPassword,
        partner?.map()
    )

fun AccountEntity.map() =
    AccountModel(
        user.profile.fullName,
        user.profile.email,
        user.profile.phone,
        locale,
        currency,
        distanceUnit,
        groups,
        user.termsAccepted,
        carrierId,
        password,
        repeatedPassword
    )

package com.kg.gettransfer.cache.model

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity

@Entity(tableName = AccountEntity.ENTITY_NAME)
class AccountCached(
        fullName: String?,
        email: String?,
        phone: String?,
        @ColumnInfo(name = AccountEntity.LOCALE) val locale: String?,
        @ColumnInfo(name = AccountEntity.CURRENCY) val currency: String?,
        @ColumnInfo(name = AccountEntity.DISTANCE_UNIT) val distanceUnit: String?,
        @ColumnInfo(name = AccountEntity.GROUPS) val groups: StringList?,
        @ColumnInfo(name = UserEntity.TERMS_ACCEPTED) val termsAccepted: Boolean?,
        @ColumnInfo(name = AccountEntity.CARRIER_ID) val carrierId: Long?,
        @PrimaryKey(autoGenerate = true) val id: Long = 0
) : ProfileCached(fullName, email, phone)

fun AccountCached.map() =
    AccountEntity(
        UserEntity(ProfileEntity(fullName, email, phone), termsAccepted ?: false),
        locale,
        currency,
        distanceUnit,
        groups?.list,
        carrierId
    )

fun AccountEntity.map() =
    AccountCached(
        user.profile.fullName,
        user.profile.email,
        user.profile.phone,
        locale,
        currency,
        distanceUnit,
        groups?.let { StringList(it) },
        user.termsAccepted,
        carrierId
    )

package com.kg.gettransfer.cache.model

import android.arch.persistence.room.*

import com.kg.gettransfer.cache.StringListConverter

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity

@Entity(tableName = AccountEntity.ENTITY_NAME)
@TypeConverters(StringListConverter::class)
data class AccountCached(@Embedded val user: UserCached,
                         @ColumnInfo(name = AccountEntity.LOCALE) val locale: String?,
                         @ColumnInfo(name = AccountEntity.CURRENCY) val currency: String?,
                         @ColumnInfo(name = AccountEntity.DISTANCE_UNIT) val distanceUnit: String?,
                         @ColumnInfo(name = AccountEntity.GROUPS) val groups: List<String>?,
                         @ColumnInfo(name = AccountEntity.CARRIER_ID) val carrierId: Long?)

data class UserCached(@Embedded val profile: ProfileCached,
                      @ColumnInfo(name = UserEntity.TERMS_ACCEPTED) val termsAccepted: Boolean = true)

data class ProfileCached(@ColumnInfo(name = ProfileEntity.FULL_NAME) val name: String?,
                         @ColumnInfo(name = ProfileEntity.EMAIL) val email: String?,
                         @ColumnInfo(name = ProfileEntity.PHONE) val phone: String?)

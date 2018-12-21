package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import com.kg.gettransfer.data.model.AccountEntity
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

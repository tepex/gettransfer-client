package com.kg.gettransfer.cache.model

import android.arch.persistence.room.*
import com.kg.gettransfer.cache.StringListConverter
import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity
import java.util.*

const val TABLE_ACCOUNT = "Account"

@Entity(tableName = TABLE_ACCOUNT)
@TypeConverters(StringListConverter::class)
data class AccountCached(@Embedded(prefix = AccountEntity.USER) val user: UserCached,
                         val locale: String?,
                         val currency: String?,
                         @ColumnInfo(name = AccountEntity.DISTANCE_UNIT) val distanceUnit: String?,
                         val groups: Array<String>?,
                         @PrimaryKey
                         @ColumnInfo(name = AccountEntity.CARRIER_ID) val carrierId: Long?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountCached

        if (!Arrays.equals(groups, other.groups)) return false

        return true
    }

    override fun hashCode(): Int {
        return groups?.let { Arrays.hashCode(it) } ?: 0
    }
}

data class UserCached(@Embedded(prefix = ProfileEntity.PROFILE) val profile: ProfileCached,
                      @ColumnInfo(name = UserEntity.TERMS_ACCEPTED) val termsAccepted: Boolean = true)

data class ProfileCached(val name: String?, val email: String?, val phone: String?)

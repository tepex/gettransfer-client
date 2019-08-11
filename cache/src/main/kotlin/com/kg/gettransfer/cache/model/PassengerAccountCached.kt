package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.kg.gettransfer.data.model.PassengerAccountEntity

data class PassengerAccountCached(
    @ColumnInfo(name = PassengerAccountEntity.ID)        val id: Long,
    @Embedded(prefix = PassengerAccountEntity.PROFILE)   val profile: ProfileCached,
    @ColumnInfo(name = PassengerAccountEntity.LAST_SEEN) var lastSeen: String?
)

fun PassengerAccountCached.map() = PassengerAccountEntity(id, profile.map(), lastSeen)

fun PassengerAccountEntity.map() = PassengerAccountCached(id, profile.map(), lastSeen)

package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import com.kg.gettransfer.data.model.ProfileEntity

open class ProfileCached(
    @ColumnInfo(name = ProfileEntity.FULL_NAME) val fullName: String?,
    @ColumnInfo(name = ProfileEntity.EMAIL) val email: String?,
    @ColumnInfo(name = ProfileEntity.PHONE) val phone: String?
)

fun ProfileCached.map() = ProfileEntity(fullName, email, phone)

fun ProfileEntity.map() = ProfileCached(fullName, email, phone)

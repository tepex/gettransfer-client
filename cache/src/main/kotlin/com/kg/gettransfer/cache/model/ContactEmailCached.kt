package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import com.kg.gettransfer.data.model.ContactEmailEntity
import kotlinx.serialization.Serializable

@Serializable
data class ContactEmailCached(
    @ColumnInfo(name = ContactEmailEntity.ID) val id: String,
    @ColumnInfo(name = ContactEmailEntity.EMAIL) val email: String
)

@Serializable
data class ContactEmailsCachedList(val list: List<ContactEmailCached>)

fun ContactEmailCached.map() = ContactEmailEntity(id, email)

fun ContactEmailEntity.map() = ContactEmailCached(id, email)


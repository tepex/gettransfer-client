package com.kg.gettransfer.sys.cache

import android.arch.persistence.room.ColumnInfo

import com.kg.gettransfer.sys.data.ContactEmailEntity

import kotlinx.serialization.Serializable

@Serializable
data class ContactEmailModel(
    @ColumnInfo(name = ContactEmailEntity.ID) val id: String,
    @ColumnInfo(name = ContactEmailEntity.EMAIL) val email: String
)

@Serializable
data class ContactEmailModelList(val list: List<ContactEmailModel>)

fun ContactEmailModel.map() = ContactEmailEntity(id, email)

fun ContactEmailEntity.map() = ContactEmailModel(id, email)

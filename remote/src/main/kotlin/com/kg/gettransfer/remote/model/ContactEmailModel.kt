package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.ContactEmailEntity

data class ContactEmailModel(
    @SerializedName(ContactEmailEntity.ID) @Expose val id: String,
    @SerializedName(ContactEmailEntity.EMAIL) @Expose val email: String
)

fun ContactEmailModel.map() = ContactEmailEntity(id, email)

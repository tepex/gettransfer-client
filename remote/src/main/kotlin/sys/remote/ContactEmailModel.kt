package com.kg.gettransfer.sys.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.sys.data.ContactEmailEntity

data class ContactEmailModel(
    @SerializedName(ContactEmailEntity.ID) @Expose val id: String,
    @SerializedName(ContactEmailEntity.EMAIL) @Expose val email: String
)

class ContactEmailsWrapperModel : ArrayList<ContactEmailModel>()

fun ContactEmailModel.map() = ContactEmailEntity(id, email)

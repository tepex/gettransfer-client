package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.LocaleEntity

data class LocaleModel(
    @SerializedName(LocaleEntity.CODE) @Expose val code: String,
    @SerializedName(LocaleEntity.TITLE) @Expose val title: String
)

fun LocaleModel.map() = LocaleEntity(code, title)

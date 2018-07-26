package com.kg.gettransfer.data.model.secondary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Language(
        @Expose
        @SerializedName("code")
        var code: String? = null,

        @Expose
        @SerializedName("title")
        var title: String? = null): RealmObject()

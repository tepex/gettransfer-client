package com.kg.gettransfer.data.model.secondary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Locale(
        @Expose
        @SerializedName("code")
        var code: String = "",

        @Expose
        @SerializedName("title")
        var title: String = ""): RealmObject()

package com.kg.gettransfer.realm.secondary


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 07/03/2018.
 */


@RealmClass
open class Locale(
        @Expose
        @SerializedName("code")
        var code: String = "",

        @Expose
        @SerializedName("title")
        var title: String = "")

    : RealmObject()
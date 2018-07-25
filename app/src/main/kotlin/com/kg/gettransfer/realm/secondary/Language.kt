package com.kg.gettransfer.realm.secondary


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 02/03/2018.
 */


@RealmClass
open class Language(
        @Expose
        @SerializedName("code")
        var code: String? = null,

        @Expose
        @SerializedName("title")
        var title: String? = null)

    : RealmObject()
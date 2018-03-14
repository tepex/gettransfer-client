package com.kg.gettransfer.modules.http.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class Trip(
        @Expose
        @SerializedName("date")
        var date: String,

        @Expose
        @SerializedName("time")
        var time: String
)
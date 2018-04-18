package com.kg.gettransfer.modules.http.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class PriceRange {
    @Expose
    @SerializedName("min")
    var min: String? = null
    @Expose
    @SerializedName("max")
    var max: String? = null
    @Expose
    @SerializedName("book_now")
    var bookNow: String? = null
}
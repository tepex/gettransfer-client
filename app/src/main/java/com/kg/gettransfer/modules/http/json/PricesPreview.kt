package com.kg.gettransfer.modules.http.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class PricesPreview {
    @Expose
    @SerializedName("min")
    var min: Double = 0.0
    @Expose
    @SerializedName("max")
    var max: Double = 0.0
}
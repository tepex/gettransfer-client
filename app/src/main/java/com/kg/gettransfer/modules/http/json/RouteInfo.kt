package com.kg.gettransfer.modules.http.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.realm.TransportType


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class RouteInfoRaw {
    @Expose
    @SerializedName("min")
    var distance: Double = 0.0
    @Expose
    @SerializedName("max")
    var duration: Double = 0.0
    @Expose
    @SerializedName("prices")
    var prices: Map<String, PriceRange>? = null
}

class RouteInfo {
    @Expose
    @SerializedName("min")
    var distance: Double = 0.0
    @Expose
    @SerializedName("max")
    var duration: Double = 0.0
    @Expose
    @SerializedName("prices")
    var prices: Map<TransportType, PriceRange>? = null
}
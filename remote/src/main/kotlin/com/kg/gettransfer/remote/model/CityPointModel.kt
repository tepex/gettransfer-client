package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CityPointModel(@SerializedName("name") @Expose val name: String,
                     @SerializedName("point") @Expose val point: String,
                     @SerializedName("place_id") @Expose val placeId: String?)

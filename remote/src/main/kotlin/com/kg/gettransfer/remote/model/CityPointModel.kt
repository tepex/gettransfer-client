package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CityPointModel(@SerializedName("name") @Expose var name: String,
                     @SerializedName("point") @Expose var point: String,
                     @SerializedName("place_id") @Expose var placeId: String?)

package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiCityPoint(@SerializedName("name") @Expose var name: String,
                   @SerializedName("point") @Expose var point: String,
                   @SerializedName("place_id") @Expose var placeId: String?)

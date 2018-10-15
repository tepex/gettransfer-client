package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VehicleBaseModel(@SerializedName("name") @Expose var name: String,
                       @SerializedName("registration_number") @Expose var registrationNumber: String)

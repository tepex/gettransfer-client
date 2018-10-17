package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class VehicleBaseModel(@SerializedName("name") @Expose val name: String,
                            @SerializedName("registration_number") @Expose val registrationNumber: String)

package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiTransportType(@SerializedName("id") @Expose val id: String,
                       @SerializedName("pax_max") @Expose val paxMax: Int,
                       @SerializedName("luggage_max") @Expose val luggageMax: Int)

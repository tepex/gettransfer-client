package com.kg.gettransfer.network


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransferPOJO(@Expose
                   @SerializedName("distance")
                   val distance: Int,
                   @Expose
                   @SerializedName("time")
                   val time: Int,
                   @Expose
                   @SerializedName("from")
                   val from: Map<String, String>,
                   @Expose
                   @SerializedName("to")
                   val to: Map<String, String>,
                   @Expose
                   @SerializedName("date_to")
                   val dateTo: String,
                   @Expose
                   @SerializedName("time_to")
                   val timeTo: String,
                   @Expose
                   @SerializedName("transport_types")
                   val transportTypes: IntArray,
                   @Expose
                   @SerializedName("pax")
                   val pax: Int,
                   @Expose
                   @SerializedName("name_sign")
                   val nameSign: String,
                   @Expose
                   @SerializedName("passenger_profile")
                   val passengerProfile: Map<String, Map<String, String>>)


class TransferFieldPOJO(@Expose
                        @SerializedName("transfer")
                        val transfer: TransferPOJO)
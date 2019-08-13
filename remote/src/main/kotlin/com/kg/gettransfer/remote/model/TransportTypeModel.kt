package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.TransportTypeEntity

/**
 * Wrapper is used to intercept API `transport_types` and convert it into List<TransportTypeModel> with origin order.
 */
class TransportTypesWrapperModel : ArrayList<TransportTypeModel>()

data class TransportTypeModel(
    @SerializedName(TransportTypeEntity.ID) @Expose val id: String,
    @SerializedName(TransportTypeEntity.PAX_MAX) @Expose val paxMax: Int,
    @SerializedName(TransportTypeEntity.LUGGAGE_MAX) @Expose val luggageMax: Int
)

fun TransportTypeModel.map() = TransportTypeEntity(id, paxMax, luggageMax)

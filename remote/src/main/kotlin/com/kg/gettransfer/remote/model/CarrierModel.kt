package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.CarrierEntity

class CarrierModel(
    @SerializedName(CarrierEntity.ID) @Expose val id: Long,
    @SerializedName(CarrierEntity.TITLE) @Expose val title: String?,
    @SerializedName(CarrierEntity.EMAIL) @Expose val email: String?,
    @SerializedName(CarrierEntity.PHONE) @Expose val phone: String?,
    @SerializedName(CarrierEntity.APPROVED) @Expose val approved: Boolean,
    @SerializedName(CarrierEntity.COMPLETED_TRANSFERS) @Expose val completedTransfers: Int,
    @SerializedName(CarrierEntity.LANGUAGES) @Expose val languages: List<LocaleModel>,
    @SerializedName(CarrierEntity.RATINGS) @Expose val ratings: RatingsModel,
    @SerializedName(CarrierEntity.CAN_UPDATE_OFFERS) @Expose val canUpdateOffers: Boolean?
)

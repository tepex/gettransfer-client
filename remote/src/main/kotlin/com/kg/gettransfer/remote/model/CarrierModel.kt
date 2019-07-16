package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.CarrierEntity
import com.kg.gettransfer.data.model.ProfileEntity

data class CarrierModel(
    @SerializedName(CarrierEntity.ID) @Expose val id: Long,
    @SerializedName(ProfileEntity.TITLE) @Expose val title: String?,
    @SerializedName(ProfileEntity.EMAIL) @Expose val email: String?,
    @SerializedName(ProfileEntity.PHONE) @Expose val phone: String?,
    @SerializedName(CarrierEntity.APPROVED) @Expose val approved: Boolean,
    @SerializedName(CarrierEntity.COMPLETED_TRANSFERS) @Expose val completedTransfers: Int,
    @SerializedName(CarrierEntity.LANGUAGES) @Expose val languages: List<LocaleModel>,
    @SerializedName(CarrierEntity.RATINGS) @Expose val ratings: RatingsModel,
    @SerializedName(CarrierEntity.CAN_UPDATE_OFFERS) @Expose val canUpdateOffers: Boolean?
)

fun CarrierModel.map() =
    CarrierEntity(
        id,
        ProfileEntity(title, email, phone),
        approved,
        completedTransfers,
        languages.map { it.map() },
        ratings.map(),
        canUpdateOffers
    )

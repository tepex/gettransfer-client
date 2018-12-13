package com.kg.gettransfer.data.model

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CarrierEntity(
    @Optional @SerialName(PROFILE) val profile: ProfileEntity? = null,
    @SerialName(APPROVED) val approved: Boolean,
    @SerialName(COMPLETED_TRANSFERS) val completedTransfers: Int,
    @SerialName(LANGUAGES) val languages: List<LocaleEntity>,
    @SerialName(RATINGS) val ratings: RatingsEntity,
    @Optional @SerialName(CAN_UPDATE_OFFERS) val canUpdateOffers: Boolean? = false
) {

    companion object {
        const val ENTITY_NAME         = "carrier"
        const val PROFILE             = "profile"
        const val APPROVED            = "approved"
        const val COMPLETED_TRANSFERS = "completed_transfers"
        const val LANGUAGES           = "languages"
        const val RATINGS             = "ratings"
        const val CAN_UPDATE_OFFERS   = "can_update_offers"
    }
}

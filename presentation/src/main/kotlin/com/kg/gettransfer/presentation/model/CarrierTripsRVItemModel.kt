package com.kg.gettransfer.presentation.model

data class CarrierTripsRVItemModel(
    val type: Type,
    val titleText: String?,
    val isToday: Boolean?,
    val item: CarrierTripBaseModel?
) {

    enum class Type {
        TITLE,
        SUBTITLE,
        ITEM,
        END_TODAY_VIEW
    }
}

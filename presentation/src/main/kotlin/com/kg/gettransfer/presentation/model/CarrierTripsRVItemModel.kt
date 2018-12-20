package com.kg.gettransfer.presentation.model

data class CarrierTripsRVItemModel(
    val type: String,
    val titleText: String?,
    val isToday: Boolean?,
    val item: CarrierTripBaseModel?
) {

    companion object {
        const val TYPE_TITLE          = "type_title"
        const val TYPE_SUBTITLE       = "type_subtitle"
        const val TYPE_ITEM           = "type_item"
        const val TYPE_END_TODAY_VIEW = "type_end_today_view"
    }
}

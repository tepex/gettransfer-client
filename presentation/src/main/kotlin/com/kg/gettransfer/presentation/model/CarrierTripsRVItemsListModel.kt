package com.kg.gettransfer.presentation.model

data class CarrierTripsRVItemsListModel(
    val itemsList: List<CarrierTripsRVItemModel>,
    val startTodayPosition: Int,
    val endTodayPosition: Int
)

package com.kg.gettransfer.presentation.model

data class CarrierModel(
    val profile: ProfileModel?,
    val approved: Boolean,
    val completedTransfers: Int,
    val languages: List<LocaleModel>,
    val ratings: RatingsModel,
    val canUpdateOffers: Boolean = false
)

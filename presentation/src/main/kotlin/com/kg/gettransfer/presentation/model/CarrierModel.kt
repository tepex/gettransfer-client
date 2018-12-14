package com.kg.gettransfer.presentation.model

class CarrierModel(
    val id: Long,
    val profile: ProfileModel?,
    val approved: Boolean,
    val completedTransfers: Int,
    val languages: List<LocaleModel>,
    val ratings: RatingsModel,
    val canUpdateOffers: Boolean = false
)

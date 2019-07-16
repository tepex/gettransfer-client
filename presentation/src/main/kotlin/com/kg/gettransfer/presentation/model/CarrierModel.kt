package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.Ratings

data class CarrierModel(
    val id: Long,
    val profile: ProfileModel?,
    val approved: Boolean,
    val completedTransfers: Int,
    val languages: List<LocaleModel>,
    val ratings: Ratings,
    val canUpdateOffers: Boolean = false
)

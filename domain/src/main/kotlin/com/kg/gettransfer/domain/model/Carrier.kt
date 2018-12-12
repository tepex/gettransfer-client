package com.kg.gettransfer.domain.model

import java.util.Locale

data class Carrier(
    override val id: Long,
    val profile: Profile,
    val approved: Boolean,
    val completedTransfers: Int,
    val languages: List<Locale>,
    val ratings: Ratings,
    val canUpdateOffers: Boolean
) : Entity()

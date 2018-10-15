package com.kg.gettransfer.presentation.model

import java.util.Locale

class OfferModel(val id: Long,
                 val driver: ProfileModel?,
                 val vehicle: VehicleModel,
                 val priceDefault: String,
                 val pricePreferred: String?,
                 val carrierId: Long,
                 val completedTransfers: Int,
                 val wifi: Boolean,
                 val refreshments: Boolean,
                 val averageRating: Double?,
                 val priceAmount: Double,
                 val pricePercentage30: String,
                 val carrierLanguages: List<Locale>)

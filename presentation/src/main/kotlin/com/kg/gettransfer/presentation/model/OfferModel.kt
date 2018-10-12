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
                 val ratings: RatingsModel,
                 val priceAmount: Double,
                 val pricePercentage30: String,
                 val vehiclePhotos: List<String>,
                 val carrierLanguages: List<Locale>,
                 val vehicleColor: String)

class RatingsModel(val average: Double?,
                   val vehicle: Double?,
                   val driver: Double?,
                   val fair: Double?)

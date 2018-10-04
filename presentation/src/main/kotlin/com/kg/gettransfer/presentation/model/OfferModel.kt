package com.kg.gettransfer.presentation.model

import java.util.Locale

class OfferModel(val driverName: String?,
                 val driverEmail: String?,
                 val driverPhone: String?,
                 val transportType: String,
                 val transportName: String,
                 val transportNumber: String,
                 val priceDefault: String,
                 val pricePreferred: String?,
                 val paxMax: Int,
                 val baggageMax: Int,
                 val transportYear: Int,
                 val carrierId: Long,
                 val completedTransfers: Int,
                 val wifi: Boolean,
                 val refreshments: Boolean,
                 val averageRating: Double?,
                 val priceAmount: Double,
                 val vehiclePhotos: List<String>,
                 val carrierLanguages: List<Locale>,
                 val vehicleColor: String)

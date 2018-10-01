package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.DistanceUnit

class TransferModel(val id: Long,
                    val status: String,
                    val from: String,
                    val to: String,
                    val dateTime: String,
                    val distance: Int?,
                    val distanceUnit: DistanceUnit,
                    val countPassengers: Int,
                    val nameSign: String?,
                    val countChilds: Int,
                    val flightNumber: String?,
                    val comment: String?,
                    val transportTypes: List<TransportTypeModel>,
                    val paidSum: String,
                    val paidPercentage: Int,
                    val remainToPay: String,
                    val price: String?,
                    val relevantCarriersCount: Int?)

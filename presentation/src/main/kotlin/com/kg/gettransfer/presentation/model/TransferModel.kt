package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.DistanceUnit

class TransferModel(val id: Long,
                    val from: String,
                    val to: String,
                    val dateTime: String,
                    val distance: String,
                    val countPassengers: Int,
                    val nameSign: String?,
                    val countChilds: Int,
                    val flightNumber: String?,
                    val comment: String?,
                    val transportTypes: List<TransportTypeModel>,
                    val paidSum: String,
                    val paidPercentage: Int,
                    val remainToPay: String,
                    val price: String)

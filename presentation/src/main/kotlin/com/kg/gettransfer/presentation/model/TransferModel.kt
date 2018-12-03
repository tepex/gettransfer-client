package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.DistanceUnit

import java.util.Date
import java.util.Locale

class TransferModel(val id: Long,
                    val status: String,
                    val from: String,
                    val to: String?,
                    val createdAt: Date,
                    val dateTime: Date,
                    val locale: Locale,
                    val distance: Int?,
                    val distanceUnit: DistanceUnit,
                    val countPassengers: Int,
                    val nameSign: String?,
                    val countChilds: Int,
                    val flightNumber: String?,
                    val comment: String?,
                    val transportTypes: List<TransportTypeModel>,
                    val paidSum: String?,
                    val paidPercentage: Int,
                    val remainToPay: String?,
                    val price: String?,
                    val relevantCarriersCount: Int?,
                    val checkOffers: Boolean,
                    val refund_date: Date?,
                    val time: Int?,
                    val duration: Int?,
                    val statusCategory: String,
                    val timeToTransfer: Int)

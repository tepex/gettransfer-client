package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.kg.gettransfer.data.model.OfferEntity

@Entity(tableName = OfferEntity.ENTITY_NAME)
data class OfferCached(
    @PrimaryKey @ColumnInfo(name = OfferEntity.ID)     val id: Long,
    @ColumnInfo(name = OfferEntity.TRANSFER_ID)        var transferId: Long?,
    @ColumnInfo(name = OfferEntity.STATUS)             val status: String,
    @ColumnInfo(name = OfferEntity.CURRENCY)           val currency: String,
    @ColumnInfo(name = OfferEntity.WIFI)               val wifi: Boolean,
    @ColumnInfo(name = OfferEntity.WITH_NAME_SIGN)     val isWithNameSign: Boolean?,
    @ColumnInfo(name = OfferEntity.REFRESHMENTS)       val refreshments: Boolean,
    @ColumnInfo(name = OfferEntity.CHARGER)            val charger: Boolean,
    @ColumnInfo(name = OfferEntity.CREATED_AT)         val createdAt: String,
    @ColumnInfo(name = OfferEntity.UPDATED_AT)         val updatedAt: String?,
    @Embedded(prefix = OfferEntity.PRICE)              val price: PriceCached,
    @Embedded(prefix = OfferEntity.RATINGS)            val ratings: RatingsCached?,
    @ColumnInfo(name = OfferEntity.PASSENGER_FEEDBACK) val passengerFeedback: String?,
    @Embedded(prefix = OfferEntity.CARRIER)            val carrier: CarrierCached,
    @Embedded(prefix = OfferEntity.VEHICLE)            val vehicle: VehicleCached,
    @Embedded(prefix = OfferEntity.DRIVER)             val driver: ProfileCached?,
    @ColumnInfo(name = OfferEntity.WHEELCHAIR)         val wheelchair: Boolean,
    @ColumnInfo(name = OfferEntity.ARMORED)            val armored: Boolean,
    @ColumnInfo(name = OfferEntity.AVAILABLE_UNTIL)    val availableUntil: String?
)

fun OfferCached.map() =
    OfferEntity(
        id,
        transferId,
        status,
        currency,
        wifi,
        isWithNameSign,
        refreshments,
        charger,
        createdAt,
        updatedAt,
        price.map(),
        ratings?.map(),
        passengerFeedback,
        carrier.map(),
        vehicle.map(),
        driver?.map(),
        wheelchair,
        armored,
        availableUntil
    )

fun OfferEntity.map() =
    OfferCached(
        id,
        transferId,
        status,
        currency,
        wifi,
        isWithNameSign,
        refreshments,
        charger,
        createdAt,
        updatedAt,
        price.map(),
        ratings?.map(),
        passengerFeedback,
        carrier.map(),
        vehicle.map(),
        driver?.map(),
        wheelchair,
        armored,
        availableUntil
    )

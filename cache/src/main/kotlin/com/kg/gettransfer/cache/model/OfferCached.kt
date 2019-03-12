package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kg.gettransfer.data.model.OfferEntity

@Entity(tableName = OfferEntity.ENTITY_NAME)
data class OfferCached(
        @PrimaryKey @ColumnInfo(name = OfferEntity.ID) val id: Long,
        @ColumnInfo(name = OfferEntity.TRANSFER_ID) var transferId: Long?,
        @ColumnInfo(name = OfferEntity.STATUS) val status: String,
        @ColumnInfo(name = OfferEntity.WIFI) val wifi: Boolean,
        @ColumnInfo(name = OfferEntity.REFRESHMENTS) val refreshments: Boolean,
        @ColumnInfo(name = OfferEntity.CHARGER) val charger: Boolean,
        @ColumnInfo(name = OfferEntity.CREATED_AT) val createdAt: String,
        @ColumnInfo(name = OfferEntity.UPDATED_AT) val updatedAt: String?,
        @Embedded(prefix = OfferEntity.PRICE) val price: PriceCached,
        @Embedded(prefix = OfferEntity.RATINGS) val ratings: RatingsCached?,
        @ColumnInfo(name = OfferEntity.PASSENGER_FEEDBACK) val passengerFeedback: String?,
        @Embedded(prefix = OfferEntity.CARRIER) val carrier: CarrierCached,
        @Embedded(prefix = OfferEntity.VEHICLE) val vehicle: VehicleCached,
        @Embedded(prefix = OfferEntity.DRIVER) val driver: ProfileCached?
)
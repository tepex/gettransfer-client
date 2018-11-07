package com.kg.gettransfer.cache.model

import android.arch.persistence.room.*
import com.kg.gettransfer.cache.StringListConverter

import com.kg.gettransfer.cache.dao.OfferCachedDao
import com.kg.gettransfer.data.model.*

const val TABLE_OFFER = "Offer"

@Entity(tableName = TABLE_OFFER)
@TypeConverters(StringListConverter::class)
data class OfferCached(@PrimaryKey var id: Long,
                       @ColumnInfo(name = OfferEntity.TRANSFER_ID)val transferId: Long,
                       @ColumnInfo(name = OfferEntity.STATUS) val status: String,
                       @ColumnInfo(name = OfferEntity.WIFI) val wifi: Boolean,
                       @ColumnInfo(name = OfferEntity.REFRESHMENTS) val refreshments: Boolean,
                       @ColumnInfo(name = OfferEntity.CREATED_AT) val createdAt: String,
                       @ColumnInfo(name = OfferEntity.UPDATED_AT) val updatedAt: String?,
                       @ColumnInfo(name = OfferEntity.PRICE) val price: PriceCached,
                       @ColumnInfo(name = OfferEntity.RATINGS) val ratings: RatingsCached?,
                       @ColumnInfo(name = OfferEntity.PASSENGER_FEEDBACK) val passengerFeedback: String? = null,
                       @ColumnInfo(name = OfferEntity.CARRIER) val carrier: CarrierCached,
                       @ColumnInfo(name = OfferEntity.VEHICLE) val vehicle: VehicleCached,
                       @ColumnInfo(name = OfferEntity.DRIVER) val driver: ProfileCached? = null)

data class PriceCached(@ColumnInfo(name = PriceEntity.BASE) 
                       @Embedded
                       val base: MoneyCached,
                       @ColumnInfo(name = PriceEntity.PERCENTAGE_30) val percentage30: String,
                       @ColumnInfo(name = PriceEntity.PERCENTAGE_70) val percentage70: String,
                       @ColumnInfo(name = PriceEntity.AMOUNT) val amount: Double)


data class CarrierCached(@ColumnInfo(name = CarrierEntity.ID) val id: Long,
                         @ColumnInfo(name = CarrierEntity.TITLE) val title: String? = null,
                         @ColumnInfo(name = CarrierEntity.EMAIL) val email: String? = null,
                         @ColumnInfo(name = CarrierEntity.PHONE) val phone: String? = null,
                         @ColumnInfo(name = CarrierEntity.APPROVED) val approved: Boolean,
                         @ColumnInfo(name = CarrierEntity.COMPLETED_TRANSFERS) val completedTransfers: Int,
                         @ColumnInfo(name = CarrierEntity.LANGUAGES) val languages: List<LocaleEntityCached>,
                         @ColumnInfo(name = CarrierEntity.RATINGS) val ratings: RatingsCached,
                         @ColumnInfo(name = CarrierEntity.CAN_UPDATE_OFFERS) val canUpdateOffers: Boolean? = false)

data class RatingsCached(@ColumnInfo(name = RatingsEntity.AVERAGE) val average: Float?,
                         @ColumnInfo(name = RatingsEntity.VEHICLE) val vehicle: Float?,
                         @ColumnInfo(name = RatingsEntity.DRIVER) val driver: Float?,
                         @ColumnInfo(name = RatingsEntity.FAIR) val fair: Float?)


data class VehicleCached(@ColumnInfo(name = VehicleEntity.NAME) val name: String,
                         @ColumnInfo(name = VehicleEntity.REGISTRATION_NUMBER) val registrationNumber: String,
                         @ColumnInfo(name = VehicleEntity.YEAR) val year: Int,
                         @ColumnInfo(name = VehicleEntity.COLOR) val color: String?,
                         @ColumnInfo(name = VehicleEntity.TRANSPORT_TYPE_ID) val transportTypeId: String,
                         @ColumnInfo(name = VehicleEntity.PAX_MAX) val paxMax: Int,
                         @ColumnInfo(name = VehicleEntity.LUGGAGE_MAX) val luggageMax: Int,
                         @ColumnInfo(name = VehicleEntity.PHOTOS) val photos: List<String>)


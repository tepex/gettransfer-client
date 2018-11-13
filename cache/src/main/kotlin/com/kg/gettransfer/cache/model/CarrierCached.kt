package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import com.kg.gettransfer.data.model.CarrierEntity
import com.kg.gettransfer.data.model.RatingsEntity

@Entity(tableName = CarrierEntity.ENTITY_NAME)
data class CarrierCached(@PrimaryKey @ColumnInfo(name = CarrierEntity.ID) val id: Long,
                         @ColumnInfo(name = CarrierEntity.TITLE) val title: String? = null,
                         @ColumnInfo(name = CarrierEntity.EMAIL) val email: String? = null,
                         @ColumnInfo(name = CarrierEntity.PHONE) val phone: String? = null,
                         @ColumnInfo(name = CarrierEntity.APPROVED) val approved: Boolean,
                         @ColumnInfo(name = CarrierEntity.COMPLETED_TRANSFERS) val completedTransfers: Int,
                         @ColumnInfo(name = CarrierEntity.LANGUAGES) val languages: LocaleCachedList,
                         @Embedded(prefix = CarrierEntity.RATINGS) val ratings: RatingsCached,
                         @ColumnInfo(name = CarrierEntity.CAN_UPDATE_OFFERS) val canUpdateOffers: Boolean? = false)

data class RatingsCached(@ColumnInfo(name = RatingsEntity.AVERAGE) val average: Float?,
                         @ColumnInfo(name = RatingsEntity.VEHICLE) val vehicle: Float?,
                         @ColumnInfo(name = RatingsEntity.DRIVER) val driver: Float?,
                         @ColumnInfo(name = RatingsEntity.FAIR) val fair: Float?)

package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kg.gettransfer.data.model.CarrierEntity

@Entity(tableName = CarrierEntity.ENTITY_NAME)
data class CarrierCached(
    @PrimaryKey @ColumnInfo(name = CarrierEntity.ID) val id: Long,
    @Embedded(prefix = CarrierEntity.PROFILE) val profile: ProfileCached?,
    @ColumnInfo(name = CarrierEntity.APPROVED) val approved: Boolean,
    @ColumnInfo(name = CarrierEntity.COMPLETED_TRANSFERS) val completedTransfers: Int,
    @ColumnInfo(name = CarrierEntity.LANGUAGES) val languages: LocaleCachedList,
    @Embedded(prefix = CarrierEntity.RATINGS) val ratings: RatingsCached,
    @ColumnInfo(name = CarrierEntity.CAN_UPDATE_OFFERS) val canUpdateOffers: Boolean? = false
)

fun CarrierCached.map() =
    CarrierEntity(
        id,
        profile?.map(),
        approved,
        completedTransfers,
        languages.list.map { it.map() },
        ratings.map(),
        canUpdateOffers
    )

fun CarrierEntity.map() =
    CarrierCached(
        id,
        profile?.map(),
        approved,
        completedTransfers,
        LocaleCachedList(languages.map { it.map() }),
        ratings.map(),
        canUpdateOffers
    )

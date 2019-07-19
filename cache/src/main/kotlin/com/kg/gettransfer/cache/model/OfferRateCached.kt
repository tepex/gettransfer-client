package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import com.kg.gettransfer.data.model.OfferRateEntity

@Entity(tableName = OfferRateEntity.ENTITY_NAME)
data class OfferRateCached(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = OfferRateEntity.ID) val id: Long = 0,
    @ColumnInfo(name = OfferRateEntity.OFFER_ID) val offerId: Long,
    @Embedded(prefix = OfferRateEntity.REVIEW_RATE) val reviewRate: ReviewRateCached)

fun OfferRateCached.map() = OfferRateEntity(id, offerId, reviewRate.map())
fun OfferRateEntity.map() = OfferRateCached(0L, offerId, reviewRate.map())
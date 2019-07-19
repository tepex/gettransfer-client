package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import com.kg.gettransfer.data.model.OfferFeedbackEntity

@Entity(tableName = OfferFeedbackEntity.ENTITY_NAME)
data class OfferFeedbackCached(
    @PrimaryKey @ColumnInfo(name = OfferFeedbackEntity.OFFER_ID) val offerId: Long,
    @ColumnInfo(name = OfferFeedbackEntity.COMMENT) val comment: String)

fun OfferFeedbackEntity.map() = OfferFeedbackCached(offerId, comment)
fun OfferFeedbackCached.map() = OfferFeedbackEntity(offerId, comment)
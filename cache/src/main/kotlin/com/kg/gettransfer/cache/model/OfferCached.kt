package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import com.kg.gettransfer.cache.dao.OfferCachedDao

const val TABLE_OFFER = "Offer"

@Entity(tableName = TABLE_OFFER)
data class OfferCached(@PrimaryKey var id: Long,
                       val transferId: Long)

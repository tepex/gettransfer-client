package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo

import com.kg.gettransfer.data.model.TransportTypeEntity

import kotlinx.serialization.Serializable

@Serializable
data class TransportTypeCached(@ColumnInfo(name = TransportTypeEntity.ID) val id: String,
                               @ColumnInfo(name = TransportTypeEntity.PAX_MAX) val paxMax: Int,
                               @ColumnInfo(name = TransportTypeEntity.LUGGAGE_MAX) val luggageMax: Int)

@Serializable
data class TransportTypesCachedList(val list: List<TransportTypeCached>)

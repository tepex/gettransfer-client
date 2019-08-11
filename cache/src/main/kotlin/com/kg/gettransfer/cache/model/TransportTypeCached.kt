package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import com.kg.gettransfer.data.model.TransportTypeEntity
import kotlinx.serialization.Serializable

@Serializable
data class TransportTypeCached(
    @ColumnInfo(name = TransportTypeEntity.ID) val id: String,
    @ColumnInfo(name = TransportTypeEntity.PAX_MAX) val paxMax: Int,
    @ColumnInfo(name = TransportTypeEntity.LUGGAGE_MAX) val luggageMax: Int
)

@Serializable
data class TransportTypesCachedList(val list: List<TransportTypeCached>)

fun TransportTypeCached.map() = TransportTypeEntity(id, paxMax, luggageMax)

fun TransportTypeEntity.map() = TransportTypeCached(id, paxMax, luggageMax)

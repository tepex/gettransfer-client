package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kg.gettransfer.data.model.RouteInfoEntity

@Entity(tableName = RouteInfoEntity.ENTITY_NAME)
data class RouteInfoCached(
    @PrimaryKey(autoGenerate = true)                      val id: Long = 0,
    @ColumnInfo(name = RouteInfoEntity.FROM_POINT)        val fromPoint: String,
    @ColumnInfo(name = RouteInfoEntity.TO_POINT)          val toPoint: String,
    @ColumnInfo(name = RouteInfoEntity.SUCCESS)           val success: Boolean,
    @ColumnInfo(name = RouteInfoEntity.DISTANCE)          val distance: Int?,
    @ColumnInfo(name = RouteInfoEntity.DURATION)          val duration: Int?,
    @ColumnInfo(name = RouteInfoEntity.WATERTAXI)         val watertaxi: Boolean,
    @ColumnInfo(name = RouteInfoEntity.POLYLINES)         val polyLines: StringList,
    @ColumnInfo(name = RouteInfoEntity.OVERVIEW_POLYLINE) val overviewPolyline: String?,
    @ColumnInfo(name = RouteInfoEntity.HINTS_TO_COMMENTS) val hintsToComments: StringList?
)

fun RouteInfoCached.map() =
    RouteInfoEntity(
        success,
        distance,
        duration,
        mapOf(),
        watertaxi,
        polyLines.list,
        overviewPolyline,
        hintsToComments?.list
    )

fun RouteInfoEntity.map(from: String, to: String) =
    RouteInfoCached(
        0,
        from,
        to,
        success,
        distance,
        duration,
        watertaxi,
        StringList(polyLines),
        overviewPolyline,
        hintsToComments?.let { StringList(it) }
    )

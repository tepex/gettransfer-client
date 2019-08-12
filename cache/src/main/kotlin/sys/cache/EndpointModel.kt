package com.kg.gettransfer.sys.cache

import androidx.room.ColumnInfo

import com.kg.gettransfer.sys.data.EndpointEntity

import kotlinx.serialization.Serializable

@Serializable
data class EndpointModel(
    @ColumnInfo(name = EndpointEntity.NAME) val name: String,
    @ColumnInfo(name = EndpointEntity.KEY) val key: String,
    @ColumnInfo(name = EndpointEntity.URL) val url: String,
    @ColumnInfo(name = EndpointEntity.IS_DEMO) val isDemo: Int,
    @ColumnInfo(name = EndpointEntity.IS_DEV) val isDev: Int
)

fun EndpointModel.map() =
    EndpointEntity(
        name = name,
        key = key,
        url = url,
        isDemo = if (isDemo == 1) true else false,
        isDev = if (isDev == 1) true else false
    )

fun EndpointEntity.map() =
    EndpointModel(
        name = name,
        key = key,
        url = url,
        isDemo = if (isDemo) 1 else 0,
        isDev = if (isDev) 1 else 0
    )

package com.kg.gettransfer.sys.cache

import androidx.room.ColumnInfo

import com.kg.gettransfer.sys.data.BuildsConfigsEntity

import kotlinx.serialization.Serializable

@Serializable
data class BuildsConfigsCached(
    @ColumnInfo(name = BuildsConfigsEntity.UPDATE_REQUIRED) val updateRequired: Boolean
)

@Serializable
data class BuildsConfigsCachedMap(val map: Map<String, BuildsConfigsCached>)

fun BuildsConfigsCached.map() = BuildsConfigsEntity(updateRequired)

fun BuildsConfigsEntity.map() = BuildsConfigsCached(updateRequired)

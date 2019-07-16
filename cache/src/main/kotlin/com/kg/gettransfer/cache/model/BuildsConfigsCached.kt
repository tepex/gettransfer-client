package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo

import com.kg.gettransfer.data.model.BuildsConfigsEntity

import kotlinx.serialization.Serializable

@Serializable
data class BuildsConfigsCached(
    @ColumnInfo(name = BuildsConfigsEntity.UPDATE_REQUIRED) val updateRequired: Boolean
)

@Serializable
data class BuildsConfigsCachedMap(val map: Map<String, BuildsConfigsCached>)

fun BuildsConfigsCached.map() = BuildsConfigsEntity(updateRequired)

fun BuildsConfigsEntity.map() = BuildsConfigsCached(updateRequired)

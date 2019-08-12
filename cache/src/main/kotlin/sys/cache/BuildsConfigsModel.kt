package com.kg.gettransfer.sys.cache

import androidx.room.ColumnInfo

import com.kg.gettransfer.sys.data.BuildsConfigsEntity

import kotlinx.serialization.Serializable

@Serializable
data class BuildsConfigsModel(
    @ColumnInfo(name = BuildsConfigsEntity.UPDATE_REQUIRED) val updateRequired: Boolean
)

@Serializable
data class BuildsConfigsModelMap(val map: Map<String, BuildsConfigsModel>)

fun BuildsConfigsModel.map() = BuildsConfigsEntity(updateRequired)

fun BuildsConfigsEntity.map() = BuildsConfigsModel(updateRequired)

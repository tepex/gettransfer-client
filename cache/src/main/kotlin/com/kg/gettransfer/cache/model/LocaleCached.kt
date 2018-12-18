package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo

import com.kg.gettransfer.data.model.LocaleEntity

import kotlinx.serialization.Serializable

@Serializable
data class LocaleCached(
    @ColumnInfo(name = LocaleEntity.CODE) val code: String,
    @ColumnInfo(name = LocaleEntity.TITLE) val title: String
)

@Serializable
data class LocaleCachedList(val list: List<LocaleCached>)

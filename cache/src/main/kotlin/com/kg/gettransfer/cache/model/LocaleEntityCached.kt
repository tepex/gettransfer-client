package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import com.kg.gettransfer.data.model.LocaleEntity

data class LocaleEntityCached(@ColumnInfo(name = LocaleEntity.CODE) val code: String,
                              @ColumnInfo(name = LocaleEntity.TITLE) val title: String)
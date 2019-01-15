package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import com.kg.gettransfer.data.model.MoneyEntity
import kotlinx.serialization.Serializable

@Serializable
data class MoneyCached(
    @ColumnInfo(name = MoneyEntity.DEFAULT) var def: String = "",
    @ColumnInfo(name = MoneyEntity.PREFERRED) var preferred: String? = ""
)

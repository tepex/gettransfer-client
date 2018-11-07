package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import com.kg.gettransfer.data.model.MoneyEntity

data class MoneyCached(@ColumnInfo(name = MoneyEntity.DEFAULT) var default: String = "",
                       @ColumnInfo(name = MoneyEntity.PREFERRED) var preferred: String? = "")
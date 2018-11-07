package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import com.kg.gettransfer.data.model.CityPointEntity

data class CityPointCached(var name: String, var point: String,
                           @ColumnInfo(name = CityPointEntity.PLACE_ID) var placeId: String? = "")
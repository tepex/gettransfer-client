package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import com.kg.gettransfer.data.model.VehicleInfoEntity

data class VehicleInfoCached(
        @ColumnInfo(name = VehicleInfoEntity.NAME) val name: String,
        @ColumnInfo(name = VehicleInfoEntity.REGISTRATION_NUMBER) val registrationNumber: String
)
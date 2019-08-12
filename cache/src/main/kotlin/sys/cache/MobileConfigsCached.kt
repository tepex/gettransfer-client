package com.kg.gettransfer.sys.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.kg.gettransfer.sys.data.MobileConfigsEntity

@Entity(tableName = MobileConfigsEntity.ENTITY_NAME)
data class MobileConfigsCached(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = MobileConfigsEntity.INTERNAL_PUSH_SHOW_DELAY) val pushShowDelay: Int,
    @ColumnInfo(name = MobileConfigsEntity.ORDER_MINIMUM_MINUTES) val orderMinimumMinutes: Int,
    @ColumnInfo(name = MobileConfigsEntity.LICENSE_URL) val termsUrl: String,
    @ColumnInfo(name = MobileConfigsEntity.SMS_RESEND_DELAY_SEC) val smsResendDelaySec: Int?,
    @ColumnInfo(name = MobileConfigsEntity.DRIVER_APP_NOTIFY) val driverAppNotify: Int,
    @ColumnInfo(name = MobileConfigsEntity.DRIVER_MODE_BLOCK) val driverModeBlock: Int,
    @ColumnInfo(name = MobileConfigsEntity.BUILDS_CONFIGS) val buildsConfigs: BuildsConfigsCachedMap?
)

fun MobileConfigsCached.map() =
    MobileConfigsEntity(
        pushShowDelay,
        orderMinimumMinutes,
        termsUrl,
        smsResendDelaySec,
        if (driverAppNotify == 1) true else false,
        if (driverModeBlock == 1) true else false,
        buildsConfigs?.map?.mapValues { it.value.map() }
    )

fun MobileConfigsEntity.map() =
    MobileConfigsCached(
        0L,
        pushShowDelay,
        orderMinimumMinutes,
        termsUrl,
        smsResendDelaySec,
        if (isDriverAppNotify) 1 else 0,
        if (isDriverModeBlock) 1 else 0,
        buildsConfigs?.let { configs -> BuildsConfigsCachedMap(configs.mapValues { it.value.map() }) }
    )

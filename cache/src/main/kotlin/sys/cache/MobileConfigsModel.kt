package com.kg.gettransfer.sys.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.kg.gettransfer.sys.data.MobileConfigsEntity

@Entity(tableName = MobileConfigsEntity.ENTITY_NAME)
data class MobileConfigsModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = MobileConfigsEntity.INTERNAL_PUSH_SHOW_DELAY) val pushShowDelay: Int,
    @ColumnInfo(name = MobileConfigsEntity.ORDER_MINIMUM_MINUTES) val orderMinimumMinutes: Int,
    @ColumnInfo(name = MobileConfigsEntity.LICENSE_URL) val termsUrl: String,
    @ColumnInfo(name = MobileConfigsEntity.SMS_RESEND_DELAY_SEC) val smsResendDelaySec: Int?,
    @ColumnInfo(name = MobileConfigsEntity.DRIVER_APP_NOTIFY) val isDriverAppNotify: Boolean,
    @ColumnInfo(name = MobileConfigsEntity.DRIVER_MODE_BLOCK) val isDriverModeBlock: Boolean,
    @ColumnInfo(name = MobileConfigsEntity.BUILDS_CONFIGS) val buildsConfigs: BuildsConfigsModelMap?
)

fun MobileConfigsModel.map() =
    MobileConfigsEntity(
        pushShowDelay,
        orderMinimumMinutes,
        termsUrl,
        smsResendDelaySec,
        isDriverAppNotify,
        isDriverModeBlock,
        buildsConfigs?.map?.mapValues { it.value.map() }
    )

fun MobileConfigsEntity.map() =
    MobileConfigsModel(
        0L,
        pushShowDelay,
        orderMinimumMinutes,
        termsUrl,
        smsResendDelaySec,
        isDriverAppNotify,
        isDriverModeBlock,
        buildsConfigs?.let { configs -> BuildsConfigsModelMap(configs.mapValues { it.value.map() }) }
    )

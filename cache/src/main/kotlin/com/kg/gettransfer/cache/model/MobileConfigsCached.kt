package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kg.gettransfer.data.model.MobileConfigEntity

@Entity(tableName = MobileConfigEntity.ENTITY_NAME)
data class MobileConfigsCached(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = MobileConfigEntity.INTERNAL_PUSH_SHOW_DELAY) val pushShowDelay: Int,
    @ColumnInfo(name = MobileConfigEntity.ORDER_MINIMUM_MINUTES) val orderMinimumMinutes: Int,
    @ColumnInfo(name = MobileConfigEntity.LICENSE_URL) val termsUrl: String,
    @ColumnInfo(name = MobileConfigEntity.SMS_RESEND_DELAY_SEC) val smsResendDelaySec: Int?,
    @ColumnInfo(name = MobileConfigEntity.BUILDS_CONFIGS) val buildsConfigs: BuildsConfigsCachedMap?
)

fun MobileConfigsCached.map() =
    MobileConfigEntity(
        pushShowDelay,
        orderMinimumMinutes,
        termsUrl,
        smsResendDelaySec,
        buildsConfigs?.map?.mapValues { it.value.map() }
    )

fun MobileConfigEntity.map() =
    MobileConfigsCached(
        0L,
        pushShowDelay,
        orderMinimumMinutes,
        termsUrl,
        smsResendDelaySec,
        buildsConfigs?.let { configs -> BuildsConfigsCachedMap(configs.mapValues { it.value.map() }) }
    )

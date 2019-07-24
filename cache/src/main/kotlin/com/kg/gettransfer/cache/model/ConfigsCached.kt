package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import com.kg.gettransfer.data.model.ConfigsEntity

import kotlinx.serialization.Serializable

@Entity(tableName = ConfigsEntity.ENTITY_NAME)
data class ConfigsCached(
    @ColumnInfo(name = ConfigsEntity.TRANSPORT_TYPES) val transportTypes: TransportTypesCachedList,
    @ColumnInfo(name = ConfigsEntity.AVAILABLE_LOCALES) val availableLocales: LocaleCachedList,
    @ColumnInfo(name = ConfigsEntity.PAYMENT_COMMISSION) val paymentCommission: Float,
    @ColumnInfo(name = ConfigsEntity.SUPPORTED_CURRENCIES) val supportedCurrencies: CurrencyCachedList,
    @ColumnInfo(name = ConfigsEntity.SUPPORTED_DISTANCE_UNITS) val supportedDistanceUnits: StringList,
    @ColumnInfo(name = ConfigsEntity.CONTACT_EMAILS) val contactEmails: ContactEmailsCachedList,
    @PrimaryKey(autoGenerate = true) val id: Long = 15
)

fun ConfigsCached.map() =
    ConfigsEntity(
        transportTypes.list.map { it.map() },
        availableLocales.list.map { it.map() },
        paymentCommission,
        supportedCurrencies.list.map { it.map() },
        supportedDistanceUnits.list,
        contactEmails.list.map { it.map() }
    )

fun ConfigsEntity.map() =
    ConfigsCached(
        TransportTypesCachedList(transportTypes.map { it.map() }),
        LocaleCachedList(availableLocales.map { it.map() }),
        paymentCommission,
        CurrencyCachedList(supportedCurrencies.map { it.map() }),
        StringList(supportedDistanceUnits),
        ContactEmailsCachedList(contactEmails.map { it.map() })
    )

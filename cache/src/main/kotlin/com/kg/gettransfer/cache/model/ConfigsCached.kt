package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.CurrencyEntity
import com.kg.gettransfer.data.model.PaypalCredentialsEntity
import kotlinx.serialization.Serializable

@Entity(tableName = ConfigsEntity.ENTITY_NAME)
data class ConfigsCached(
    @ColumnInfo(name = ConfigsEntity.TRANSPORT_TYPES) val transportTypes: TransportTypesCachedList,
    @ColumnInfo(name = ConfigsEntity.AVAILABLE_LOCALES) val availableLocales: LocaleCachedList,
    @ColumnInfo(name = ConfigsEntity.PAYMENT_COMMISSION) val paymentCommission: Float,
    @ColumnInfo(name = ConfigsEntity.SUPPORTED_CURRENCIES) val supportedCurrencies: CurrencyCachedList,
    @ColumnInfo(name = ConfigsEntity.SUPPORTED_DISTANCE_UNITS) val supportedDistanceUnits: StringList,
    @PrimaryKey(autoGenerate = true) val id: Long = 15
)

data class PaypalCredentialsCached(
    @ColumnInfo(name = PaypalCredentialsEntity.ID) val id: String,
    @ColumnInfo(name = PaypalCredentialsEntity.ENV) val env: String
)

@Serializable
data class CurrencyCached(
    @ColumnInfo(name = CurrencyEntity.ISO_CODE) val code: String,
    @ColumnInfo(name = CurrencyEntity.SYMBOL) val symbol: String
)

@Serializable
data class CurrencyCachedList(val list: List<CurrencyCached>)

fun PaypalCredentialsCached.map() = PaypalCredentialsEntity(id, env)

fun PaypalCredentialsEntity.map() = PaypalCredentialsCached(id, env)

fun CurrencyCached.map() = CurrencyEntity(code, symbol)

fun CurrencyEntity.map() = CurrencyCached(code, symbol)

fun ConfigsCached.map() =
    ConfigsEntity(
        transportTypes.list.map { it.map() },
        availableLocales.list.map { it.map() },
        paymentCommission,
        supportedCurrencies.list.map { it.map() },
        supportedDistanceUnits.list
    )

fun ConfigsEntity.map() =
    ConfigsCached(
        TransportTypesCachedList(transportTypes.map { it.map() }),
        LocaleCachedList(availableLocales.map { it.map() }),
        paymentCommission,
        CurrencyCachedList(supportedCurrencies.map { it.map() }),
        StringList(supportedDistanceUnits)
    )

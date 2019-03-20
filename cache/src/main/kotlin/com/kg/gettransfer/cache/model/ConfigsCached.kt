package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import com.kg.gettransfer.data.model.CardGatewaysEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.CurrencyEntity
import com.kg.gettransfer.data.model.PaypalCredentialsEntity

import kotlinx.serialization.Serializable

@Entity(tableName = ConfigsEntity.ENTITY_NAME)
data class ConfigsCached(
    @ColumnInfo(name = ConfigsEntity.TRANSPORT_TYPES) val transportTypes: TransportTypesCachedList,
    //@Embedded(prefix = ConfigsEntity.PAYPAL_CREDENTIALS) val paypalCredentials: PaypalCredentialsCached,
    @ColumnInfo(name = ConfigsEntity.AVAILABLE_LOCALES) val availableLocales: LocaleCachedList,
    //@ColumnInfo(name = ConfigsEntity.PREFERRED_LOCALE) val preferredLocale: String,
    //@Embedded(prefix = ConfigsEntity.CARD_GATEWAYS) val cardGateways: CardGatewaysCached,
    //@ColumnInfo(name = ConfigsEntity.DEFAULT_CARD_GATEWAYS) val defaultCardGateways: String,
    @ColumnInfo(name = ConfigsEntity.PAYMENT_COMMISSION) val paymentCommission: Float,
    @ColumnInfo(name = ConfigsEntity.SUPPORTED_CURRENCIES) val supportedCurrencies: CurrencyCachedList,
    @ColumnInfo(name = ConfigsEntity.SUPPORTED_DISTANCE_UNITS) val supportedDistanceUnits: StringList,
    //@ColumnInfo(name = ConfigsEntity.OFFICE_PHONE) val officePhone: String,
    //@ColumnInfo(name = ConfigsEntity.BASE_URL) val baseUrl: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 14
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

data class CardGatewaysCached(
    @ColumnInfo(name = CardGatewaysEntity.DEFAULT) val def: String,
    @ColumnInfo(name = CardGatewaysEntity.ISO_COUNTRY_CODE) val countryCode: String?
)

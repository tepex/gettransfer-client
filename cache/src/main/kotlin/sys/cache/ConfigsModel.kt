package com.kg.gettransfer.sys.cache

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.kg.gettransfer.cache.model.CurrencyCachedList
import com.kg.gettransfer.cache.model.LocaleCachedList
import com.kg.gettransfer.cache.model.StringList
import com.kg.gettransfer.cache.model.TransportTypesCachedList
import com.kg.gettransfer.cache.model.map

import com.kg.gettransfer.sys.data.ConfigsEntity
import sys.cache.CheckoutCredentialsModel
import sys.cache.GooglePayCredentialsModel
import sys.cache.map

@Entity(tableName = ConfigsEntity.ENTITY_NAME)
data class ConfigsModel(
    @ColumnInfo(name = ConfigsEntity.TRANSPORT_TYPES)          val transportTypes: TransportTypesCachedList,
    @ColumnInfo(name = ConfigsEntity.AVAILABLE_LOCALES)        val availableLocales: LocaleCachedList,
    @ColumnInfo(name = ConfigsEntity.PAYMENT_COMMISSION)       val paymentCommission: Float,
    @ColumnInfo(name = ConfigsEntity.SUPPORTED_CURRENCIES)     val supportedCurrencies: CurrencyCachedList,
    @ColumnInfo(name = ConfigsEntity.SUPPORTED_DISTANCE_UNITS) val supportedDistanceUnits: StringList,
    @ColumnInfo(name = ConfigsEntity.CONTACT_EMAILS)           val contactEmails: ContactEmailModelList,
    @Embedded(prefix = ConfigsEntity.CHECKOUT_CREDENTIALS)     val checkoutCredentials: CheckoutCredentialsModel,
    @Embedded(prefix = ConfigsEntity.GOOGLEPAY_CREDENTIALS)    val googlePayCredentials: GooglePayCredentialsModel,
    @ColumnInfo(name = ConfigsEntity.DEFAULT_CARD_GATEWAY)     val defaultCardGateway: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 15
)

fun ConfigsModel.map() =
    ConfigsEntity(
        transportTypes.list.map { it.map() },
        availableLocales.list.map { it.map() },
        paymentCommission,
        supportedCurrencies.list.map { it.map() },
        supportedDistanceUnits.list,
        contactEmails.list.map { it.map() },
        checkoutCredentials.map(),
        googlePayCredentials.map(),
        defaultCardGateway
    )

fun ConfigsEntity.map() =
    ConfigsModel(
        TransportTypesCachedList(transportTypes.map { it.map() }),
        LocaleCachedList(availableLocales.map { it.map() }),
        paymentCommission,
        CurrencyCachedList(supportedCurrencies.map { it.map() }),
        StringList(supportedDistanceUnits),
        ContactEmailModelList(contactEmails.map { it.map() }),
        checkoutCredentials.map(),
        googlePayCredentials.map(),
        defaultCardGateway
    )

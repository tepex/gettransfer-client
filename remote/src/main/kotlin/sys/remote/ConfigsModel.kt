package com.kg.gettransfer.sys.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.remote.model.CurrencyModel
import com.kg.gettransfer.remote.model.LocaleModel
import com.kg.gettransfer.remote.model.TransportTypesWrapperModel
import com.kg.gettransfer.remote.model.map

import com.kg.gettransfer.sys.data.ConfigsEntity
import sys.remote.CheckoutcomCredentialsModel
import sys.remote.GooglePayCredentialsModel
import sys.remote.map

data class ConfigsModel(
    @SerializedName(ConfigsEntity.TRANSPORT_TYPES)          @Expose val transportTypes: TransportTypesWrapperModel,
    @SerializedName(ConfigsEntity.AVAILABLE_LOCALES)        @Expose val availableLocales: List<LocaleModel>,
    @SerializedName(ConfigsEntity.PAYMENT_COMMISSION)       @Expose val paymentCommission: Float,
    @SerializedName(ConfigsEntity.SUPPORTED_CURRENCIES)     @Expose val supportedCurrencies: List<CurrencyModel>,
    @SerializedName(ConfigsEntity.SUPPORTED_DISTANCE_UNITS) @Expose val supportedDistanceUnits: List<String>,
    @SerializedName(ConfigsEntity.CONTACT_EMAILS)           @Expose val contactEmails: ContactEmailsWrapperModel,
    @Suppress("MaximumLineLength", "MaxLineLength")
    @SerializedName(ConfigsEntity.CHECKOUTCOM_CREDENTIALS)  @Expose val checkoutcomCredentials: CheckoutcomCredentialsModel,
    @SerializedName(ConfigsEntity.GOOGLEPAY_CREDENTIALS)    @Expose val googlePayCredentials: GooglePayCredentialsModel,
    @SerializedName(ConfigsEntity.DEFAULT_CARD_GATEWAY)     @Expose val defaultCardGateway: String
)

fun ConfigsModel.map() =
    ConfigsEntity(
        transportTypes.map { it.map() },
        availableLocales.map { it.map() },
        paymentCommission,
        supportedCurrencies.map { it.map() },
        supportedDistanceUnits,
        contactEmails.map { it.map() },
        checkoutcomCredentials.map(),
        googlePayCredentials.map(),
        defaultCardGateway
    )

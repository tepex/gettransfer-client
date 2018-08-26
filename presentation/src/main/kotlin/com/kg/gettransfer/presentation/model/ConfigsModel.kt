package com.kg.gettransfer.presentation.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.TransportType

import java.util.Currency

import kotlin.reflect.*

import timber.log.Timber

class ConfigsModel(private val delegate: Configs) {
    val currencies: List<CurrencyModel>
    val locales: List<LocaleModel>
    val transportTypes: List<TransportTypeModel>
    val distanceUnits: List<String>
        get() = delegate.supportedDistanceUnits

    init {
        currencies = delegate.supportedCurrencies.map {
            CurrencyModel("${it.displayName} (${it.hackedSymbol})", it.currencyCode)
        }
        locales = delegate.availableLocales.map { LocaleModel(it.getDisplayLanguage(it), it.language) }
        transportTypes = delegate.transportTypes.map { TransportTypeModel(it) }
    }
}

class CurrencyModel(val name: String, val code: String): CharSequence by name {
    override fun toString(): String = name
}

class LocaleModel(val name: String, val code: String): CharSequence by name {
    override fun toString(): String = name
}

class TransportTypeModel(val delegate: TransportType, var checked: Boolean = false) {
    @DrawableRes
    val imageId: Int
    @StringRes
    val nameId: Int
    
    init {
    	val nameRes = R.string::class.members.find( { it.name == "transport_type_${delegate.id}" } )
        nameId = (nameRes?.call() as Int?) ?: R.string.transport_type_unknown
        val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_${delegate.id}" } )
        imageId = (imageRes?.call() as Int?) ?: R.drawable.ic_transport_type_unknown
    }
}

/* Dirty hack for ruble, Yuan ¥ */
val Currency.hackedSymbol: String
    get() = when(currencyCode) {
        "RUB" -> "\u20BD"
        "CNY" -> "¥"
        else  -> symbol
    }

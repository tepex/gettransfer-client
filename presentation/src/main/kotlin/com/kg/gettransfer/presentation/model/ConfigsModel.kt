package com.kg.gettransfer.presentation.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.TransportType

import java.util.Currency
import java.util.Locale

import kotlin.reflect.*

import timber.log.Timber

class ConfigsModel(private val delegate: Configs) {
    val currencies: List<CurrencyModel>
    val locales: List<LocaleModel>
    val transportTypes: List<TransportTypeModel>
    val distanceUnits: List<DistanceUnitModel>

    init {
        currencies = delegate.supportedCurrencies.map { CurrencyModel(it) }
        locales = delegate.availableLocales.map { LocaleModel(it) }
        transportTypes = delegate.transportTypes.map { TransportTypeModel(it) }
        distanceUnits = delegate.supportedDistanceUnits.map { DistanceUnitModel(it) }
    }
}

class CurrencyModel(val delegate: Currency): CharSequence {
    val name = "${delegate.displayName} ($symbol)"
    override val length = name.length
    val code = delegate.currencyCode
    val symbol: String
        /* Dirty hack for ruble, Yuan ¥ */
        get() = when(delegate.currencyCode) {
            "RUB" -> "\u20BD"
            "CNY" -> "¥"
            else  -> delegate.symbol
        }
    
    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}

class LocaleModel(val delegate: Locale): CharSequence {
    val name = delegate.getDisplayLanguage(delegate)
    override val length = name.length
    
    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
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

class DistanceUnitModel(val delegate: DistanceUnit): CharSequence {
    val name = delegate.name
    override val length = name.length
    
    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}

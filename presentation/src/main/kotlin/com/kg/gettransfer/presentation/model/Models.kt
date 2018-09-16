package com.kg.gettransfer.presentation.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.TransportType

import java.util.Currency
import java.util.Locale

class RouteModel(val distance: Int?,
                 val distanceUnit: DistanceUnit,
                 val polyLines: List<String>,
                 val from: GTAddress,
                 val to: GTAddress)

class CurrencyModel(val delegate: Currency): CharSequence {
    val name = "${delegate.displayName} ($symbol)"
    override val length = name.length
    private val code = delegate.currencyCode
    val symbol: String
        /* Dirty hack for ruble, Yuan ¥ */
        get() = when(code) {
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

class DistanceUnitModel(val delegate: DistanceUnit): CharSequence {
    val name = delegate.name
    override val length = name.length
    
    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}

class TransportTypeModel(val id: String,
                         @StringRes val nameId: Int,
                         @DrawableRes val imageId: Int,
                         val paxMax: Int,
                         val luggageMax: Int,
                         val price: String,
                         var checked: Boolean = false)

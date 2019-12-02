package com.kg.gettransfer.presentation.model

import java.util.Locale

data class LocaleModel(val delegate: Locale) : CharSequence {

    val name = delegate.getDisplayLanguage(delegate).capitalize()
    override val length = name.length
    val locale = delegate.language.toUpperCase(Locale.US)

    override fun toString(): String = name

    override operator fun get(index: Int): Char = name.get(index)

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)

    companion object {
        val BOOK_NOW_LOCALE_DEFAULT = LocaleModel(Locale("en"))
    }
}

fun Locale.map() = LocaleModel(this)

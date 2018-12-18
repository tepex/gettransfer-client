package com.kg.gettransfer.presentation.model

import java.util.Locale

class LocaleModel(val delegate: Locale) : CharSequence {
    val name = delegate.getDisplayLanguage(delegate)
    override val length = name.length
    val locale = delegate.language.toUpperCase()

    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}

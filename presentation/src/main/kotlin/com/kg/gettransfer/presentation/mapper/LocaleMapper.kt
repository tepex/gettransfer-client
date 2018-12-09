package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.presentation.model.LocaleModel

import java.util.Locale

open class LocaleMapper : Mapper<LocaleModel, Locale> {
    override fun toView(type: Locale) = LocaleModel(type)
    override fun fromView(type: LocaleModel): Locale { throw UnsupportedOperationException() }
}
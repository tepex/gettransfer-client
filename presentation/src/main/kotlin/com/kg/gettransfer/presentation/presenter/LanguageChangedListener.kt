package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.presentation.model.LocaleModel

interface LanguageChangedListener {
    fun languageChanged(language: LocaleModel)
}

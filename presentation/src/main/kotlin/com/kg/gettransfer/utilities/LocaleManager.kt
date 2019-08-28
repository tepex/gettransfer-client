package com.kg.gettransfer.utilities

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

class LocaleManager {

    fun updateResources(context: Context, locale: Locale): ContextWrapper {
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)

        config.setLocale(locale)
        return ContextWrapper(context.createConfigurationContext(config))
    }
}
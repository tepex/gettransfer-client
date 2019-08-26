package com.kg.gettransfer.utilities

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

class LocaleManager {

    fun updateResources(context: Context, locale: Locale): ContextWrapper {
        val appContext = context.applicationContext
        Locale.setDefault(locale)

        val resources = appContext.resources
        val config = Configuration(resources.configuration)

        config.setLocale(locale)
        return ContextWrapper(appContext.createConfigurationContext(config))
    }
}
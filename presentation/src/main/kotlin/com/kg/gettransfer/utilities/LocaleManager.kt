package com.kg.gettransfer.utilities

import android.content.Context
import android.content.res.Configuration
import java.util.*

class LocaleManager {

    fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)

        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
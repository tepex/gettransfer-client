package com.kg.gettransfer.utilities

import android.content.Context
import android.os.Build
import java.util.Locale

class LocaleManager {

    fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            return updateResourcesLocale(context, locale)
        }
        return updateResourcesLocaleLegacy(context, locale)
    }

    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val config = context.resources.configuration.apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return context.createConfigurationContext(config)
    }

    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val config = resources.configuration.apply {
            this.locale = locale
            setLayoutDirection(locale)
        }
        resources.updateConfiguration(config, resources.displayMetrics)
        return context
    }
}
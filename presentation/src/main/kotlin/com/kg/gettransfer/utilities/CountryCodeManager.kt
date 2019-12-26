package com.kg.gettransfer.utilities

import android.content.Context
import android.telephony.TelephonyManager
import org.koin.core.KoinComponent

class CountryCodeManager(val context: Context) : KoinComponent {

    fun getCountryCode(defaultCountryIsoCode: String) =
        detectSIMCountry() ?: detectNetworkCountry() ?: detectLocaleCountry() ?: defaultCountryIsoCode

    private fun detectSIMCountry(): String? {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return telephonyManager.simCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun detectNetworkCountry(): String? {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return telephonyManager.networkCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun detectLocaleCountry(): String? {
        try {
            return context.resources.configuration.locale.country
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

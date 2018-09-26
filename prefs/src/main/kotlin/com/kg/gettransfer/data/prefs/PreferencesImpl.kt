package com.kg.gettransfer.data.prefs

import android.content.Context

import com.kg.gettransfer.data.model.*

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.domain.repository.Preferences

import java.util.Currency
import java.util.Locale

class PreferencesImpl(context: Context): Preferences {

    companion object {
        @JvmField val LAST_MODE = "last_mode"
    }

    private val configsPrefs = context.getSharedPreferences(CONFIGS, Context.MODE_PRIVATE)
    private val accountPrefs = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
    private var _accessToken = Preferences.INVALID_TOKEN
    
    override var accessToken: String
        get() {
            if(_accessToken == Preferences.INVALID_TOKEN) _accessToken = configsPrefs.getString(TOKEN, Preferences.INVALID_TOKEN)!!
            return _accessToken
        }
        set(value) {
            _accessToken = value
            val editor = configsPrefs.edit()
            editor.putString(TOKEN, value)
            editor.apply()
        }
       
    override var account: Account
        get() {
            var localeCode = accountPrefs.getString(ACCOUNT_LOCALE, null)
            var currencyCode = accountPrefs.getString(ACCOUNT_CURRENCY, null)
            return Account(
                    accountPrefs.getString(ACCOUNT_EMAIL, null),
                    accountPrefs.getString(ACCOUNT_PHONE, null),
                    if(localeCode != null) Locale(localeCode) else null,
                    if(currencyCode != null) Currency.getInstance(currencyCode) else null,
                    DistanceUnit.parse(accountPrefs.getString(ACCOUNT_DISTANCE_UNIT, null)),
                    accountPrefs.getString(ACCOUNT_FULL_NAME, null),
                    accountPrefs.getStringSet(ACCOUNT_GROUPS, null)?.toTypedArray(),
                    accountPrefs.getBoolean(ACCOUNT_TERMS_ACCEPTED, false))
        }
        set(value) {
            val editor = accountPrefs.edit()
            editor.putString(ACCOUNT_EMAIL, value.email)
            editor.putString(ACCOUNT_PHONE, value.phone)
            editor.putString(ACCOUNT_LOCALE, value.locale?.language)
            editor.putString(ACCOUNT_CURRENCY, value.currency?.currencyCode)
            editor.putString(ACCOUNT_DISTANCE_UNIT, value.distanceUnit?.name)
            editor.putString(ACCOUNT_FULL_NAME, value.fullName)
            editor.putStringSet(ACCOUNT_GROUPS, value.groups?.toSet())
            editor.putBoolean(ACCOUNT_TERMS_ACCEPTED, value.termsAccepted)
            editor.apply()
        }

    override var lastMode: String
        get() = configsPrefs.getString(LAST_MODE, "")!!
        set(value) {
            val editor = configsPrefs.edit()
            editor.putString(LAST_MODE, value)
            editor.apply()
        }
        
    override fun cleanAccount() {
        val editor = accountPrefs.edit()
        editor.remove(ACCOUNT_EMAIL)
        editor.remove(ACCOUNT_PHONE)
        editor.remove(ACCOUNT_LOCALE)
        editor.remove(ACCOUNT_CURRENCY)
        editor.remove(ACCOUNT_DISTANCE_UNIT)
        editor.remove(ACCOUNT_FULL_NAME)
        editor.remove(ACCOUNT_GROUPS)
        editor.remove(ACCOUNT_TERMS_ACCEPTED)
        editor.apply()
    }
}

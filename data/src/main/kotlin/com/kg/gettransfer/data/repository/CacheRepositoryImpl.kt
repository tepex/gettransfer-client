package com.kg.gettransfer.data.repository

import android.content.Context

import com.kg.gettransfer.data.model.*

import com.kg.gettransfer.domain.model.Account

import java.util.Currency
import java.util.Locale

import timber.log.Timber

class CacheRepositoryImpl(private val context: Context) {
    companion object {
        @JvmField val INVALID_TOKEN = "invalid_token"
    }
    
    private val configsPrefs = context.getSharedPreferences(CONFIGS, Context.MODE_PRIVATE)
    private val accountPrefs = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
    private var _accessToken = INVALID_TOKEN
    
    var accessToken: String
        get() {
            if(_accessToken == INVALID_TOKEN) _accessToken = configsPrefs.getString(TOKEN, INVALID_TOKEN)!!
            return _accessToken
        }
        set(value) {
            _accessToken = value
            val editor = configsPrefs.edit()
            editor.putString(TOKEN, value)
            editor.apply()
        }
       
    var account: Account
        get() {
            var localeCode = accountPrefs.getString(ACCOUNT_LOCALE, null)
            var currencyCode = accountPrefs.getString(ACCOUNT_CURRENCY, null)
            return Account(
                    accountPrefs.getString(ACCOUNT_EMAIL, null),
                    accountPrefs.getString(ACCOUNT_PHONE, null),
                    if(localeCode != null) Locale(localeCode) else null,
                    if(currencyCode != null) Currency.getInstance(currencyCode) else null,
                    accountPrefs.getString(ACCOUNT_DISTANCE_UNIT, null),
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
            editor.putString(ACCOUNT_DISTANCE_UNIT, value.distanceUnit)
            editor.putString(ACCOUNT_FULL_NAME, value.fullName)
            editor.putStringSet(ACCOUNT_GROUPS, value.groups?.toSet())
            editor.putBoolean(ACCOUNT_TERMS_ACCEPTED, value.termsAccepted)
            editor.apply()
        }
}

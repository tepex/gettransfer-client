package com.kg.gettransfer.data.repository

import android.content.Context

import com.kg.gettransfer.domain.model.Account

import java.util.Currency
import java.util.Locale

import timber.log.Timber

class CacheRepositoryImpl(private val context: Context) {
    private val configsPrefs = context.getSharedPreferences(PREFS_CONFIGS, Context.MODE_PRIVATE)
    private val accountPrefs = context.getSharedPreferences(PREFS_ACCOUNT, Context.MODE_PRIVATE)
    private var _accessToken: String? = null
    private var _account: Account = Account.EMPTY
    
    var accessToken: String?
        get() {
            if(_accessToken == null) _accessToken = configsPrefs.getString(ACCESS_TOKEN, null)
            return _accessToken
        }
        set(value) {
            _accessToken = value
            val editor = configsPrefs.edit()
            if(value != null) editor.putString(ACCESS_TOKEN, value)
            else editor.remove(ACCESS_TOKEN)
            editor.commit()
        }
       
    /* If `email` field is null, Account is empty. */
    var account: Account
        get() {
            if(_account === Account.EMPTY) {
                val email = accountPrefs.getString(ACCOUNT_EMAIL, null)
                if(email != null) _account = Account(
                    email,
                    accountPrefs.getString(ACCOUNT_PHONE, null),
                    Locale(accountPrefs.getString(ACCOUNT_LOCALE, "en")),
                    Currency.getInstance(accountPrefs.getString(ACCOUNT_CURRENCY, "USD")),
                    accountPrefs.getString(ACCOUNT_DISTANCE_UNIT, null),
                    accountPrefs.getString(ACCOUNT_FULL_NAME, null),
                    accountPrefs.getStringSet(ACCOUNT_GROUPS, null)?.toTypedArray(),
                    accountPrefs.getBoolean(ACCOUNT_TERMS_ACCEPTED, false))
            }
            Timber.d("get from prefs: %s", _account)
            return _account
        }
        set(value) {
            _account = value
            Timber.d("save to prefs: %s", value)
            val editor = accountPrefs.edit()
            if(value === Account.EMPTY) editor.remove(ACCOUNT_EMAIL)
            else {
                editor.putString(ACCOUNT_EMAIL, value.email)
                editor.putString(ACCOUNT_PHONE, value.phone)
                editor.putString(ACCOUNT_LOCALE, value.locale?.language)
                editor.putString(ACCOUNT_CURRENCY, value.currency?.currencyCode)
                editor.putString(ACCOUNT_DISTANCE_UNIT, value.distanceUnit)
                editor.putString(ACCOUNT_FULL_NAME, value.fullName)
                editor.putStringSet(ACCOUNT_GROUPS, value.groups?.toSet())
                editor.putBoolean(ACCOUNT_TERMS_ACCEPTED, value.termsAccepted)
            }
            editor.commit()
        }
        
    companion object {
	    val PREFS_CONFIGS = "configs"
	    val ACCESS_TOKEN  = "access_token"
	    
	    val PREFS_ACCOUNT          = "account"
	    val ACCOUNT_EMAIL          = "email"
	    val ACCOUNT_PHONE          = "phone"
	    val ACCOUNT_LOCALE         = "locale"
	    val ACCOUNT_CURRENCY       = "currency"
	    val ACCOUNT_DISTANCE_UNIT  = "distance_unit"
	    val ACCOUNT_FULL_NAME      = "full_name"
	    val ACCOUNT_GROUPS         = "groups"
	    val ACCOUNT_TERMS_ACCEPTED = "terms_accepted"
    }
}

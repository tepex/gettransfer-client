package com.kg.gettransfer.prefs

import android.content.Context

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.SystemCache

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.UserEntity

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.User

import java.util.Currency
import java.util.Locale

class PreferencesImpl(context: Context): Preferences {

    companion object {
        @JvmField val LAST_MODE = "last_mode"
        @JvmField val ENDPOINT  = "endpoint"
    }

    private val configsPrefs = context.getSharedPreferences(CONFIGS, Context.MODE_PRIVATE)
    private val accountPrefs = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
    private var _accessToken = SystemCache.INVALID_TOKEN
    
    override var accessToken: String
        get() {
            if(_accessToken == SystemCache.INVALID_TOKEN) _accessToken = configsPrefs.getString(TOKEN, SystemCache.INVALID_TOKEN)!!
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
            var carrierId: Long? = accountPrefs.getLong(ACCOUNT_CARRIER_ID, -1)
            if(carrierId == -1L) carrierId = null
            
            return Account(User(Profile(accountPrefs.getString(ACCOUNT_FULL_NAME, null),
                                        accountPrefs.getString(ACCOUNT_EMAIL, null),
                                        accountPrefs.getString(ACCOUNT_PHONE, null)),
                                accountPrefs.getBoolean(ACCOUNT_TERMS_ACCEPTED, false)),
                           if(localeCode != null) Locale(localeCode) else null,
                           if(currencyCode != null) Currency.getInstance(currencyCode) else null,
                           DistanceUnit.parse(accountPrefs.getString(ACCOUNT_DISTANCE_UNIT, null)),
                           accountPrefs.getStringSet(ACCOUNT_GROUPS, null)?.toTypedArray(),
                           carrierId)
        }
       
    override var account: AccountEntity 
        get() = AccountEntity(UserEntity(accountPrefs.getString(ACCOUNT_FULL_NAME, null),
                                         accountPrefs.getString(ACCOUNT_EMAIL, null),
                                         accountPrefs.getString(ACCOUNT_PHONE, null),
                                         accountPrefs.getBoolean(ACCOUNT_TERMS_ACCEPTED, false)),
                              accountPrefs.getString(ACCOUNT_LOCALE, null),
                              accountPrefs.getString(ACCOUNT_CURRENCY, null),
                              accountPrefs.getString(ACCOUNT_DISTANCE_UNIT, null),
                              accountPrefs.getStringSet(ACCOUNT_GROUPS, null)?.toTypedArray(),
                              accountPrefs.getLong(ACCOUNT_CARRIER_ID, -1))
        set(value) {
            val editor = accountPrefs.edit()
            editor.putString(ACCOUNT_EMAIL, value.user.profile.email)
            editor.putString(ACCOUNT_PHONE, value.user.profile.phone)
            editor.putString(ACCOUNT_LOCALE, value.locale?.language)
            editor.putString(ACCOUNT_CURRENCY, value.currency?.currencyCode)
            editor.putString(ACCOUNT_DISTANCE_UNIT, value.distanceUnit?.name)
            editor.putString(ACCOUNT_FULL_NAME, value.user.profile.name)
            editor.putStringSet(ACCOUNT_GROUPS, value.groups?.toSet())
            editor.putBoolean(ACCOUNT_TERMS_ACCEPTED, value.user.termsAccepted)
            if(value.carrierId == null) editor.remove(ACCOUNT_CARRIER_ID)
            else editor.putLong(ACCOUNT_CARRIER_ID, value.carrierId!!)
            editor.apply()
        }

    override var lastMode: String
        get() = configsPrefs.getString(PreferencesCache.LAST_MODE, "")!!
        set(value) {
            val editor = configsPrefs.edit()
            editor.putString(PreferencesCache.LAST_MODE, value)
            editor.apply()
        }

    override var endpoint: String
        get() = configsPrefs.getString(ENDPOINT, "")!!
        set(value){
            val editor = configsPrefs.edit()
            editor.putString(ENDPOINT, value)
            editor.apply()
        }
        
    override fun clearAccount() {
        val editor = accountPrefs.edit()
        editor.remove(ACCOUNT_EMAIL)
        editor.remove(ACCOUNT_PHONE)
        editor.remove(ACCOUNT_LOCALE)
        editor.remove(ACCOUNT_CURRENCY)
        editor.remove(ACCOUNT_DISTANCE_UNIT)
        editor.remove(ACCOUNT_FULL_NAME)
        editor.remove(ACCOUNT_GROUPS)
        editor.remove(ACCOUNT_TERMS_ACCEPTED)
        editor.remove(ACCOUNT_CARRIER_ID)
        editor.apply()
    }
}

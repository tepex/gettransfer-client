package com.kg.gettransfer.prefs

import android.content.Context

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.SystemCache

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity

class PreferencesImpl(context: Context): PreferencesCache, SystemCache {
    companion object {
        const val ACCOUNT                = "account"
        const val CONFIGS                = "configs"
        const val TOKEN                  = "token"

        const val ACCOUNT_EMAIL          = "email"
        const val ACCOUNT_PASSWORD       = "password"
        const val ACCOUNT_PHONE          = "phone"
        const val ACCOUNT_LOCALE         = "locale"
        const val ACCOUNT_CURRENCY       = "currency"
        const val ACCOUNT_DISTANCE_UNIT  = "distance_unit"
        const val ACCOUNT_FULL_NAME      = "full_name"
        const val ACCOUNT_GROUPS         = "groups"
        const val ACCOUNT_CARRIER_ID     = "carrier_id"
        const val ACCOUNT_TERMS_ACCEPTED = "terms_accepted"
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
        
    override var lastMode: String
        get() = configsPrefs.getString(PreferencesCache.LAST_MODE, "")!!
        set(value) {
            val editor = configsPrefs.edit()
            editor.putString(PreferencesCache.LAST_MODE, value)
            editor.apply()
        }

       
    override var account: AccountEntity 
        get() {
            val email = accountPrefs.getString(ACCOUNT_EMAIL, null)
            if(email == null) return AccountEntity.NO_ACCOUNT
            else return AccountEntity(UserEntity(ProfileEntity(accountPrefs.getString(ACCOUNT_FULL_NAME, null),
                                                               email,
                                                               accountPrefs.getString(ACCOUNT_PHONE, null)),
                                                               accountPrefs.getBoolean(ACCOUNT_TERMS_ACCEPTED, false)),
                                      accountPrefs.getString(ACCOUNT_LOCALE, null),
                                      accountPrefs.getString(ACCOUNT_CURRENCY, null),
                                      accountPrefs.getString(ACCOUNT_DISTANCE_UNIT, null),
                                      accountPrefs.getStringSet(ACCOUNT_GROUPS, null)?.toTypedArray(),
                                      accountPrefs.getLong(ACCOUNT_CARRIER_ID, -1))
        }
        set(value) {
            val editor = accountPrefs.edit()
            editor.putString(ACCOUNT_EMAIL, value.user.profile.email)
            editor.putString(ACCOUNT_PHONE, value.user.profile.phone)
            editor.putString(ACCOUNT_LOCALE, value.locale)
            editor.putString(ACCOUNT_CURRENCY, value.currency)
            editor.putString(ACCOUNT_DISTANCE_UNIT, value.distanceUnit)
            editor.putString(ACCOUNT_FULL_NAME, value.user.profile.name)
            editor.putStringSet(ACCOUNT_GROUPS, value.groups?.toSet())
            editor.putBoolean(ACCOUNT_TERMS_ACCEPTED, value.user.termsAccepted)
            if(value.carrierId == null) editor.remove(ACCOUNT_CARRIER_ID)
            else editor.putLong(ACCOUNT_CARRIER_ID, value.carrierId!!)
            editor.apply()
        }

    override var endpoint: String
        get() = configsPrefs.getString(PreferencesCache.ENDPOINT, "Demo")!!
        set(value) {
            val editor = configsPrefs.edit()
            editor.putString(PreferencesCache.ENDPOINT, value)
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

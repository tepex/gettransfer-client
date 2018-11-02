package com.kg.gettransfer.prefs

import android.content.Context
import android.content.SharedPreferences

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.SystemCache

import com.kg.gettransfer.data.model.*

import com.kg.gettransfer.domain.SystemListener
import com.kg.gettransfer.domain.SystemListenerManager

import kotlinx.serialization.list
import kotlinx.serialization.json.JSON

import timber.log.Timber

class PreferencesImpl(context: Context): PreferencesCache, SystemCache, SystemListenerManager {
    companion object {
        const val ACCOUNT = "account"
        const val CONFIGS = "configs"
        const val TOKEN = "token"

        const val ACCOUNT_EMAIL = "email"
        const val ACCOUNT_PASSWORD = "password"
        const val ACCOUNT_PHONE = "phone"
        const val ACCOUNT_LOCALE = "locale"
        const val ACCOUNT_CURRENCY = "currency"
        const val ACCOUNT_DISTANCE_UNIT = "distance_unit"
        const val ACCOUNT_FULL_NAME = "full_name"
        const val ACCOUNT_GROUPS = "groups"
        const val ACCOUNT_CARRIER_ID = "carrier_id"
        const val ACCOUNT_TERMS_ACCEPTED = "terms_accepted"

        const val ACCOUNT_ADDRESS_HISTORY = "history"

        const val CONFIGS_JSON = "json"
    }
    
    private val listeners = mutableSetOf<SystemListener>()

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
            listeners.forEach { it.accessTokenChanged(value) }
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
            if (value.carrierId == null) editor.remove(ACCOUNT_CARRIER_ID)
            else editor.putLong(ACCOUNT_CARRIER_ID, value.carrierId!!)
            editor.apply()
        }

    override var configs: ConfigsEntity
        get() {
            val json = configsPrefs.getString(CONFIGS_JSON, null)
            if(json != null) return JSON.parse(ConfigsEntity.serializer(), json)
            return ConfigsEntity(emptyList<TransportTypeEntity>(),
                                 PaypalCredentialsEntity("", ""),
                                 emptyList<LocaleEntity>(),
                                 "en",
                                 emptyList<CurrencyEntity>(),
                                 emptyList<String>(),
                                 CardGatewaysEntity("", ""),
                                 "",
                                 "")
        }
        set(value) {
            with(configsPrefs.edit()) {
                putString(CONFIGS_JSON, JSON.stringify(ConfigsEntity.serializer(), value))
                apply()
            }
        }

    override var endpoint: String
        get() = configsPrefs.getString(PreferencesCache.ENDPOINT, "Demo")!!
        set(value) {
            val editor = configsPrefs.edit()
            editor.putString(PreferencesCache.ENDPOINT, value)
            editor.apply()
        }

    override var isInternetAvailable: Boolean
        get() = configsPrefs.getBoolean(PreferencesCache.INTERNET, true)
        set(value) {
            val editor = configsPrefs.edit()
            editor.putBoolean(PreferencesCache.INTERNET, value)
            editor.apply()
        }

    override fun clearAccount() {
        var editor = accountPrefs.edit()
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

        editor = configsPrefs.edit()
        editor.remove(TOKEN)
        editor.apply()
        _accessToken = SystemCache.INVALID_TOKEN
    }

    override var lastAddresses: List<GTAddressEntity>
        get() {
            val json = accountPrefs.getString(ACCOUNT_ADDRESS_HISTORY, null)
            return if(json != null) JSON.parse(GTAddressEntity.serializer().list, json) else emptyList<GTAddressEntity>()
        }
        set(value) {
            with(accountPrefs.edit()) {
                putString(ACCOUNT_ADDRESS_HISTORY, JSON.stringify(GTAddressEntity.serializer().list, value))
                apply()
            }            
        }

    override fun addListener(listener: SystemListener)    { listeners.add(listener) }
    override fun removeListener(listener: SystemListener) { listeners.remove(listener) }
}

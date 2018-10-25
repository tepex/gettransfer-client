package com.kg.gettransfer.prefs

import android.content.Context
import android.content.SharedPreferences
import com.kg.gettransfer.JsonParser

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.SystemCache
import com.google.gson.Gson

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.GTAddressEntity
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity

class PreferencesImpl(context: Context) : PreferencesCache, SystemCache {


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

        const val CONFIGS_TRANSPORT_TYPES = "configs_transport_types"
        const val CONFIGS_PAYPAL_CREDITIALS = "configs_paypal_creditials"
        const val CONFIGS_AVAILABLE_LOCALES = "configs_available_locales"
        const val CONFIGS_PREFERRED_LOCALE = "configs_preferred_locale"
        const val CONFIGS_SUPPORTED_CURRENCIES = "configs_supported_currencies"
        const val CONFIGS_SUPPORTED_DISTANCE = "configs_supported_distance"
        const val CONFIGS_CARD_GATEWAYS = "configs_card_gateways"
        const val CONFIGS_OFFICE_PHONE = "configs_office_phone"
        const val CONFIGS_BASE_URL = "configs_base_url"
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
            if (value.carrierId == null) editor.remove(ACCOUNT_CARRIER_ID)
            else editor.putLong(ACCOUNT_CARRIER_ID, value.carrierId!!)
            editor.apply()
        }

    override var configs: ConfigsEntity
        get(){
            val gson = Gson()
            return ConfigsEntity(gson.fromJson(configsPrefs.getString(CONFIGS_TRANSPORT_TYPES, null), Array<TransportTypeEntity>::class.java).toList(),
                    gson.fromJson(configsPrefs.getString(CONFIGS_PAYPAL_CREDITIALS, null), PaypalCredentialsEntity::class.java),
                    gson.fromJson(configsPrefs.getString(CONFIGS_AVAILABLE_LOCALES, null), Array<LocaleEntity>::class.java).toList(),
                    configsPrefs.getString(CONFIGS_PREFERRED_LOCALE, null),
                    gson.fromJson(configsPrefs.getString(CONFIGS_SUPPORTED_CURRENCIES, null), Array<CurrencyEntity>::class.java).toList(),
                    configsPrefs.getStringSet(CONFIGS_SUPPORTED_DISTANCE, null)?.toTypedArray()!!.toList(),
                    gson.fromJson(configsPrefs.getString(CONFIGS_CARD_GATEWAYS, null), CardGatewaysEntity::class.java),
                    configsPrefs.getString(CONFIGS_OFFICE_PHONE, null),
                    configsPrefs.getString(CONFIGS_BASE_URL, null))
        }
        set(value) {
            val editor = configsPrefs.edit()
            setList(editor, CONFIGS_TRANSPORT_TYPES, value.transportTypes)
            setObject(editor, CONFIGS_PAYPAL_CREDITIALS, value.paypalCredentials)
            setList(editor, CONFIGS_AVAILABLE_LOCALES, value.availableLocales)
            editor.putString(CONFIGS_PREFERRED_LOCALE, value.preferredLocale)
            setList(editor, CONFIGS_SUPPORTED_CURRENCIES, value.supportedCurrencies)
            editor.putStringSet(CONFIGS_SUPPORTED_DISTANCE, value.supportedDistanceUnits.toSet())
            setObject(editor, CONFIGS_CARD_GATEWAYS, value.cardGateways)
            editor.putString(CONFIGS_OFFICE_PHONE, value.officePhone)
            editor.putString(CONFIGS_BASE_URL, value.baseUrl)
            editor.apply()
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

    override var lastAddresses: List<GTAddressEntity>?
        get() = JsonParser().getFromJson(accountPrefs.getString(ACCOUNT_ADDRESS_HISTORY, null))
        set(value) {
            accountPrefs.edit()
                    .putString(ACCOUNT_ADDRESS_HISTORY, JsonParser().writeToJson(value!!))
                    .apply()
        }

    fun <T> setList(editor: SharedPreferences.Editor, key: String, list: List<T>) {
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(key, json)
    }

    fun <T> setObject(editor: SharedPreferences.Editor, key:String, obj: T){
        val gson = Gson()
        val json = gson.toJson(obj)
        editor.putString(key, json)
    }

    /*fun <T> getList(preferences: SharedPreferences, key: String, qwerty: T): List<T> {
        val gson = Gson()
        val erewrw = Activ
        if(qwerty is TransportTypeEntity){}
        return gson.fromJson(preferences.getString(key, ""), Array< qwerty::class.objectInstance>::class.java).toList()
    }*/
}

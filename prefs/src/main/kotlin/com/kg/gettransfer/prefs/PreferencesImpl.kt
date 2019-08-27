package com.kg.gettransfer.prefs

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener
import com.kg.gettransfer.data.model.*

import kotlinx.serialization.list
import kotlinx.serialization.json.JSON
import java.util.*

@Suppress("WildcardImport")
class PreferencesImpl(
    context: Context,
    private val encryptPass: EncryptPass
) : PreferencesCache {

    private val listeners = mutableSetOf<PreferencesListener>()

    private val configsPrefs  = context.getSharedPreferences("configs", Context.MODE_PRIVATE)
    private val accountPrefs  = context.getSharedPreferences(AccountEntity.ENTITY_NAME, Context.MODE_PRIVATE)
    private val driverPrefs   = context.getSharedPreferences(CarrierEntity.ENTITY_NAME, Context.MODE_PRIVATE)
    private var _accessToken = INVALID_TOKEN
    private var _userEmail: String? = null
    private var _userPhone: String? = null
    private var _userPassword = INVALID_PASSWORD
    private var counterUpdated = false

    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

    override var accessToken: String
        get() {
            if (_accessToken == INVALID_TOKEN) {
                _accessToken = configsPrefs.getString(ACCESS_TOKEN, INVALID_TOKEN) ?: INVALID_TOKEN
            }
            return _accessToken
        }
        set(value) {
            _accessToken = value
            configsPrefs.edit().putString(ACCESS_TOKEN, value).apply()
            listeners.forEach { it.accessTokenChanged(value) }
        }

    override var userEmail: String?
        get() {
            if (_userEmail == null || _userEmail == INVALID_EMAIL) {
                val prefsEmail = configsPrefs.getString(USER_EMAIL, INVALID_EMAIL) ?: INVALID_EMAIL
                _userEmail = if (prefsEmail.isNotEmpty()) prefsEmail else null
            }
            return _userEmail
        }
        set(value) {
            _userEmail = value
            with(configsPrefs.edit()) {
                if (value == null) remove(USER_EMAIL) else putString(USER_EMAIL, value)
                apply()
            }
        }

    override var userPhone: String?
        get() {
            if (_userPhone == null || _userPhone == INVALID_PHONE) {
                val prefsPhone = configsPrefs.getString(USER_PHONE, INVALID_PHONE) ?: INVALID_PHONE
                _userPhone = if (prefsPhone.isNotEmpty()) prefsPhone else null
            }
            return _userPhone
        }
        set(value) {
            _userPhone = value
            with(configsPrefs.edit()) {
                if (value == null) remove(USER_PHONE) else putString(USER_PHONE, value)
                apply()
            }
        }

    override var userPassword: String
        get() {
            if (_userPassword == INVALID_PASSWORD) {
                _userPassword = configsPrefs.getString(USER_PASSWORD, INVALID_PASSWORD) ?: INVALID_PASSWORD
                encryptPass.encryptDecrypt(_userPassword)
            }
            return _userPassword
        }
        set(value) {
            _userPassword = value
            configsPrefs.edit().putString(USER_PASSWORD, encryptPass.encryptDecrypt(value)).apply()
        }


    override var mapCountNewOffers: Map<Long, Int>
        get() = getMap(MAP_NEW_OFFERS)
        set(value) {
            setMap(value, MAP_NEW_OFFERS)
        }

    override var mapCountNewMessages: Map<Long, Int>
        get() = getMap(MAP_NEW_MESSAGES)
        set(value) {
            setMap(value, MAP_NEW_MESSAGES)
        }

    override var mapCountViewedOffers: Map<Long, Int>
        get() = getMap(MAP_VIEWED_MESSAGES)
        set(value) {
            setMap(value, MAP_VIEWED_MESSAGES)
        }

    override var eventsCount: Int
        get() = configsPrefs.getInt(EVENTS_COUNT, 0)
        set(value) {
            configsPrefs.edit().putInt(EVENTS_COUNT, value).apply()
        }

    override var appLanguage: String
        get() = configsPrefs.getString(APP_LANGUAGE, Locale.getDefault().language)
        set(value) {
            configsPrefs.edit().putString(APP_LANGUAGE, value).apply()
        }

    /* === Functions === */

    private fun getMap(key: String): Map<Long, Int> {
        val json = configsPrefs.getString(key, null)
        return if (json != null) Gson().fromJson(json, genericType<Map<Long, Int>>()) else emptyMap()
    }

    private fun setMap(value: Map<Long, Int>, key: String) {
        configsPrefs.edit().putString(key, Gson().toJson(value)).apply()
    }

    override fun logout() {
        _accessToken  = INVALID_TOKEN
        _userEmail    = INVALID_EMAIL
        _userPhone    = INVALID_PHONE
        _userPassword = INVALID_PASSWORD
        with(configsPrefs.edit()) {
            remove(ACCESS_TOKEN)
            remove(USER_EMAIL)
            remove(USER_PHONE)
            remove(USER_PASSWORD)
            apply()
        }
    }

    override fun addListener(listener: PreferencesListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: PreferencesListener) {
        listeners.remove(listener)
    }

    companion object {
        const val INVALID_TOKEN       = "invalid_token"
        const val ACCESS_TOKEN        = "token"
        const val INVALID_EMAIL       = ""
        const val INVALID_PHONE       = ""
        const val INVALID_PASSWORD    = ""
        const val USER_EMAIL          = "user_email"
        const val USER_PHONE          = "user_phone"
        const val USER_PASSWORD       = "user_password"
        const val LAST_MODE           = "last_mode"
        const val LAST_MAIN_MODE      = "last_main_mode"
        const val CARRIER_TYPE_VIEW   = "last_carrier_trips_type_view"
        const val FIRST_DAY_OF_WEEK   = "first_day_of_week"
        const val FIRST_LAUNCH        = "first_launch"
        const val ONBOARDING          = "onboarding"
        const val SELECTED_FIELD      = "selected_field"
        const val ENDPOINT            = "endpoint"
        const val ADDRESS_HISTORY     = "history"
        const val APP_ENTERS_COUNT    = "enters_count"
        const val FAVORITE_TRANSPORT  = "favorite_transport"
        const val DEBUG_MENU          = "debug_menu"
        const val APP_LANGUAGE        = "app_language"

        const val MAP_NEW_OFFERS      = "map_new_offers"
        const val MAP_NEW_MESSAGES    = "map_new_messages"
        const val MAP_VIEWED_MESSAGES = "map_viewed_messages"
        const val EVENTS_COUNT        = "count_new_offers"
        const val DRIVER_IN_BG        = "back_ground_coordinates"
        const val OFFERS_VIEW         = "offers_view"

        const val FIRST_ACCESS   = 0
        const val IMMUTABLE      = -1   // user did rate app
    }
}

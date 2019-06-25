package com.kg.gettransfer.prefs

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener
import com.kg.gettransfer.data.model.*
import com.kg.gettransfer.domain.repository.CarrierTripRepository.Companion.BG_COORDINATES_NOT_ASKED

import kotlinx.serialization.list
import kotlinx.serialization.json.JSON

class PreferencesImpl(context: Context,
                      override val endpoints: List<EndpointEntity>,
                      private val defaultEndpointName: String,
                      private val encryptPass: EncryptPass): PreferencesCache {
    companion object {
        @JvmField val INVALID_TOKEN       = "invalid_token"
        @JvmField val ACCESS_TOKEN        = "token"
        @JvmField val INVALID_EMAIL       = ""
        @JvmField val INVALID_PHONE       = ""
        @JvmField val INVALID_PASSWORD    = ""
        @JvmField val USER_EMAIL          = "user_email"
        @JvmField val USER_PHONE          = "user_phone"
        @JvmField val USER_PASSWORD       = "user_password"
        @JvmField val LAST_MODE           = "last_mode"
        @JvmField val LAST_MAIN_MODE      = "last_main_mode"
        @JvmField val CARRIER_TYPE_VIEW   = "last_carrier_trips_type_view"
        @JvmField val FIRST_DAY_OF_WEEK   = "first_day_of_week"
        @JvmField val FIRST_LAUNCH        = "first_launch"
        @JvmField val ONBOARDING          = "onboarding"
        @JvmField val SELECTED_FIELD      = "selected_field"
        @JvmField val ENDPOINT            = "endpoint"
        @JvmField val ADDRESS_HISTORY     = "history"
        @JvmField val APP_ENTERS_COUNT    = "enters_count"
        @JvmField val FAVORITE_TRANSPORT  = "favorite_transport"
        @JvmField val DEBUG_MENU           = "debug_menu"

        @JvmField val MAP_NEW_OFFERS      = "map_new_offers"
        @JvmField val MAP_NEW_MESSAGES    = "map_new_messages"
        @JvmField val MAP_VIEWED_MESSAGES = "map_viewed_messages"
        @JvmField val EVENTS_COUNT        = "count_new_offers"
        @JvmField val DRIVER_IN_BG        = "back_ground_coordinates"
        @JvmField val OFFERS_VIEW         = "offers_view"

        const val FIRST_ACCESS   = 0
        const val IMMUTABLE      = -1   // user did rate app
              var counterUpdated = false
    }
    
    private val listeners = mutableSetOf<PreferencesListener>()
    
    private val configsPrefs  = context.getSharedPreferences(ConfigsEntity.ENTITY_NAME, Context.MODE_PRIVATE)
    private val accountPrefs  = context.getSharedPreferences(AccountEntity.ENTITY_NAME, Context.MODE_PRIVATE)
    private val driverPrefs   = context.getSharedPreferences(CarrierEntity.ENTITY_NAME, Context.MODE_PRIVATE)
    private var _accessToken  = INVALID_TOKEN
    private var _userEmail: String? = null
    private var _userPhone: String? = null
    private var _userPassword = INVALID_PASSWORD
    private var _endpoint: EndpointEntity? = null

    inline fun <reified T> genericType() = object: TypeToken<T>() {}.type

    override var accessToken: String
        get() {
            if(_accessToken == INVALID_TOKEN)
                _accessToken = configsPrefs.getString(ACCESS_TOKEN, INVALID_TOKEN)!!
            return _accessToken
        }
        set(value) {
            _accessToken = value
            with(configsPrefs.edit()) {
                putString(ACCESS_TOKEN, value)
                apply()
            }
            listeners.forEach { it.accessTokenChanged(value) }
        }

    override var userEmail: String?
        get() {
            if(_userEmail == null || _userEmail == INVALID_EMAIL) {
                val prefsEmail = configsPrefs.getString(USER_EMAIL, INVALID_EMAIL)!!
                _userEmail = if (prefsEmail.isNotEmpty()) prefsEmail else null
            }
            return _userEmail
        }
        set(value) {
            _userEmail = value
            with(configsPrefs.edit()) {
                if (value == null) {
                    remove(USER_EMAIL)
                } else {
                    putString(USER_EMAIL, value)
                }
                apply()
            }
        }

    override var userPhone: String?
        get() {
            if (_userPhone == null || _userPhone == INVALID_PHONE) {
                val prefsPhone = configsPrefs.getString(USER_PHONE, INVALID_PHONE)!!
                _userPhone = if (prefsPhone.isNotEmpty()) prefsPhone else null
            }
            return _userPhone
        }
        set(value) {
            _userPhone = value
            with(configsPrefs.edit()) {
                if (value == null) {
                    remove(USER_PHONE)
                } else {
                    putString(USER_PHONE, value)
                }
                apply()
            }
        }

    override var userPassword: String
        get() {
            if(_userPassword == INVALID_PASSWORD)
                _userPassword = configsPrefs.getString(USER_PASSWORD, INVALID_PASSWORD)!!.let { encryptPass.encryptDecrypt(it) }
            return _userPassword
        }
        set(value) {
            _userPassword = value
            with(configsPrefs.edit()) {
                putString(USER_PASSWORD, encryptPass.encryptDecrypt(value))
                apply()
            }
        }

    override var lastMode: String
        get() = configsPrefs.getString(LAST_MODE, "")!!
        set(value) {
            with(configsPrefs.edit()) {
                putString(LAST_MODE, value)
                apply()
            }
        }

    override var lastMainScreenMode: String
        get() = configsPrefs.getString(LAST_MAIN_MODE, "")!!
        set(value) {
            with(configsPrefs.edit()) {
                putString(LAST_MAIN_MODE, value)
                apply()
            }
        }

    override var lastCarrierTripsTypeView: String
        get() = configsPrefs.getString(CARRIER_TYPE_VIEW, "")!!
        set(value) {
            with(configsPrefs.edit()){
                putString(CARRIER_TYPE_VIEW, value)
                apply()
            }
        }

    override var firstDayOfWeek: Int
        get() = configsPrefs.getInt(FIRST_DAY_OF_WEEK, 1)
        set(value) {
            with(configsPrefs.edit()){
                putInt(FIRST_DAY_OF_WEEK, value)
                apply()
            }
        }

    override var isFirstLaunch: Boolean
        get() = configsPrefs.getBoolean(FIRST_LAUNCH, true)
        set(value) {
            with(configsPrefs.edit()) {
                putBoolean(FIRST_LAUNCH, value)
                apply()
            }
        }

    override var isOnboardingShowed: Boolean
        get() = configsPrefs.getBoolean(ONBOARDING, false)
        set(value) {
            with(configsPrefs.edit()){
                putBoolean(ONBOARDING, value)
                apply()
            }
        }

    override var selectedField: String
        get() = configsPrefs.getString(SELECTED_FIELD, "")!!
        set(value) {
            with(configsPrefs.edit()) {
                putString(SELECTED_FIELD, value)
                apply()
            }
        }

    override var endpoint: EndpointEntity
        get() {
            if(_endpoint == null) {
                val name = configsPrefs.getString(ENDPOINT, defaultEndpointName)
                _endpoint = endpoints.find { it.name == name }
            }
            return _endpoint!!
        }
        set(value) {
            _endpoint = value
            with(configsPrefs.edit()) {
                putString(ENDPOINT, value.name)
                apply()
            }
            listeners.forEach { it.endpointChanged(value) }
        }

    override var addressHistory: List<GTAddressEntity>
        get() {
            val json = accountPrefs.getString(ADDRESS_HISTORY, null)
            return if(json != null) JSON.nonstrict.parse(GTAddressEntity.serializer().list, json) else emptyList()
        }
        set(value) {
            with(accountPrefs.edit()) {
                putString(ADDRESS_HISTORY, JSON.stringify(GTAddressEntity.serializer().list, value))
                apply()
            }            
        }

    override var favoriteTransportTypes: Set<String>?
        get() = accountPrefs.getStringSet(FAVORITE_TRANSPORT, null)
        set(value) {
            accountPrefs.edit()
                    .putStringSet(FAVORITE_TRANSPORT, value)
                    .apply()
        }

    override var appEnters: Int
        get() {
            var count = accountPrefs.getInt(APP_ENTERS_COUNT, FIRST_ACCESS)
            if (count == IMMUTABLE || counterUpdated) return count
            counterUpdated = true
            appEnters = ++count
            return count
        }
        set(value) {
            accountPrefs.apply {
                if (getInt(APP_ENTERS_COUNT, FIRST_ACCESS) != IMMUTABLE)
                    edit()
                    .putInt(APP_ENTERS_COUNT, value)
                    .apply()
            }

        }

    override var mapCountNewOffers: Map<Long, Int>
        get() = getMap(MAP_NEW_OFFERS)
        set(value) { setMap(value, MAP_NEW_OFFERS) }

    override var mapCountNewMessages: Map<Long, Int>
        get() = getMap(MAP_NEW_MESSAGES)
        set(value) { setMap(value, MAP_NEW_MESSAGES) }

    override var mapCountViewedOffers: Map<Long, Int>
        get() = getMap(MAP_VIEWED_MESSAGES)
        set(value) { setMap(value, MAP_VIEWED_MESSAGES) }

    override var eventsCount: Int
        get() = configsPrefs.getInt(EVENTS_COUNT, 0)
        set(value) { configsPrefs.edit().putInt(EVENTS_COUNT, value).apply() }

    private fun getMap(key: String): Map<Long, Int> {
        val json = configsPrefs.getString(key, null)
        return if (json != null) Gson().fromJson(json, genericType<Map<Long, Int>>()) else emptyMap()
    }

    private fun setMap(value: Map<Long, Int>, key: String) {
        with(configsPrefs.edit()) {
            putString(key, Gson().toJson(value))
            apply()
        }
    }

    override fun logout() {
        _accessToken  = INVALID_TOKEN
        _userEmail    = INVALID_EMAIL
        _userPassword = INVALID_PASSWORD
        with(configsPrefs.edit()){
            remove(ACCESS_TOKEN)
            remove(USER_EMAIL)
            remove(USER_PASSWORD)
            apply()
        }
    }

    override var driverCoordinatesInBackGround: Int
        get() = driverPrefs.getInt(DRIVER_IN_BG, BG_COORDINATES_NOT_ASKED)
        set(value) {
            driverPrefs.edit()
                    .putInt(DRIVER_IN_BG, value)
                    .apply()
        }

    override var offerViewExpanded: Boolean
        get() = accountPrefs.getBoolean(OFFERS_VIEW, true)
        set(value) {
            accountPrefs
                    .edit()
                    .putBoolean(OFFERS_VIEW, value)
                    .apply()
        }

    override var isDebugMenuShowed: Boolean
        get() = configsPrefs.getBoolean(DEBUG_MENU, false)
        set(value) {
            with(configsPrefs.edit()){
                putBoolean(DEBUG_MENU, value)
                apply()
            }
        }
        
    override fun addListener(listener: PreferencesListener)    { listeners.add(listener) }
    override fun removeListener(listener: PreferencesListener) { listeners.remove(listener) }
}

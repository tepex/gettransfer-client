package com.kg.gettransfer.prefs

import android.content.Context
import android.content.SharedPreferences

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.GTAddressEntity

import kotlinx.serialization.list
import kotlinx.serialization.json.JSON
import kotlinx.serialization.parseList
import kotlinx.serialization.serializer

import timber.log.Timber

class PreferencesImpl(context: Context,
                      override val endpoints: List<EndpointEntity>): PreferencesCache {
    companion object {
        @JvmField val INVALID_TOKEN    = "invalid_token"
        @JvmField val ACCESS_TOKEN     = "token"
        @JvmField val LAST_MODE        = "last_mode"
        @JvmField val FIRST_LAUNCH     = "first_launch"
        @JvmField val ONBOARDING       = "onboarding"
        @JvmField val SELECTED_FIELD   = "selected_field"
        @JvmField val ENDPOINT         = "endpoint"
        @JvmField val ADDRESS_HISTORY  = "history"
        @JvmField val APP_ENTERS_COUNT = "enters_count"
        @JvmField val EVENTS_COUNT     = "events_count"
        @JvmField val TRANSFER_IDS     = "transfer_ids"

        const val FIRST_ACCESS         = 0
        const val IMMUTABLE            = -1   // user did rate app
    }
    
    private val listeners = mutableSetOf<PreferencesListener>()
    
    private val configsPrefs = context.getSharedPreferences(ConfigsEntity.ENTITY_NAME, Context.MODE_PRIVATE)
    private val accountPrefs = context.getSharedPreferences(AccountEntity.ENTITY_NAME, Context.MODE_PRIVATE)
    private var _accessToken = INVALID_TOKEN
    private var _endpoint: EndpointEntity? = null

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

    override var lastMode: String
        get() = configsPrefs.getString(LAST_MODE, "")!!
        set(value) {
            with(configsPrefs.edit()) {
                putString(LAST_MODE, value)
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
                val name = configsPrefs.getString(ENDPOINT, null)
                if(name != null) _endpoint = endpoints.find { it.name == name }
                if(_endpoint == null) _endpoint = endpoints.first()
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
            return if(json != null) JSON.parse(GTAddressEntity.serializer().list, json) else emptyList<GTAddressEntity>()
        }
        set(value) {
            with(accountPrefs.edit()) {
                putString(ADDRESS_HISTORY, JSON.stringify(GTAddressEntity.serializer().list, value))
                apply()
            }            
        }

    override var appEnters: Int
        get() {
            var count = accountPrefs.getInt(APP_ENTERS_COUNT, FIRST_ACCESS)
            if (count == IMMUTABLE) return count
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

    override var eventsCount: Int
        get() = configsPrefs.getInt(EVENTS_COUNT, 0)
        set(value) {
            with(configsPrefs.edit()) {
                putInt(EVENTS_COUNT, value)
                apply()
            }
        }

    override var transferIds: List<Long>
        get() {
            val json = configsPrefs.getString(TRANSFER_IDS, null)
            return if (json != null) JSON.parse(Long.serializer().list, json) else emptyList()
        }
        set(value) {
            with(configsPrefs.edit()) {
                putString(TRANSFER_IDS, JSON.stringify(Long.serializer().list, value))
                apply()
            }
        }
        
    override fun logout() {
        _accessToken = INVALID_TOKEN
        configsPrefs.edit().apply { remove(ACCESS_TOKEN) }.apply()
    }
        
    override fun addListener(listener: PreferencesListener)    { listeners.add(listener) }
    override fun removeListener(listener: PreferencesListener) { listeners.remove(listener) }
}

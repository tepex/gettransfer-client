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

import timber.log.Timber

class PreferencesImpl(context: Context,
                      override val endpoints: List<EndpointEntity>): PreferencesCache {
    companion object {
        @JvmField val INVALID_TOKEN   = "invalid_token"
        @JvmField val ACCESS_TOKEN    = "token"
        @JvmField val LAST_MODE       = "last_mode"
        @JvmField val SELECTED_FIELD  = "selected_field"
        @JvmField val ENDPOINT        = "endpoint"        
        @JvmField val ADDRESS_HISTORY = "history"
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
                _endpoint = if(name == null) endpoints.find { it.isDemo }!! else endpoints.find { it.name == name }!!
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
        
    override fun logout() {
        _accessToken = INVALID_TOKEN
        configsPrefs.edit().apply { remove(ACCESS_TOKEN) }.apply()
    }
        
    override fun addListener(listener: PreferencesListener)    { listeners.add(listener) }
    override fun removeListener(listener: PreferencesListener) { listeners.remove(listener) }
}
